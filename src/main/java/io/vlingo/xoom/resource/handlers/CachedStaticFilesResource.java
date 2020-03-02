package io.vlingo.xoom.resource.handlers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import io.vlingo.common.Completes;
import io.vlingo.http.Body;
import io.vlingo.http.Header;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.*;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.http.RequestHeader.ContentType;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.ContentLength;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

/**
 * The {@link StaticFilesResource} serves static content from the /src/main/resources/static directory of the Xoom
 * application.
 *
 * @author Kenny Bastani
 * @author Wolfgang Werner
 * @author Vaughn Vernon
 */
public class CachedStaticFilesResource extends ResourceHandler {

    // TODO: Make the cache expiration and max size limit configurable
    private final LoadingCache<String, byte[]> staticFileCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener(this::onRemoval)
            .build(new CacheLoader<String, byte[]>() {
                @Override
                @ParametersAreNonnullByDefault
                public byte[] load(String path) throws IOException {
                    return readFileFromClasspath(path);
                }
            });

    @Override
    public Resource<?> routes() {
        final RequestHandler0.Handler0 serve0 = this::serve;
        final RequestHandler1.Handler1<String> serve1 = this::serve;
        final RequestHandler2.Handler2<String, String> serve2 = this::serve;
        final RequestHandler3.Handler3<String, String, String> serve3 = this::serve;
        final RequestHandler4.Handler4<String, String, String, String> serve4 = this::serve;

        // TODO: Make the handler pool size for this resource configurable
        return resource("Static File Resource", 10,
                get("")
                        .handle(() -> redirectToApp("/")),
                get("/")
                        .handle(serve0),
                get("/static")
                        .handle(serve0),
                get("/static/")
                        .handle(() -> redirectToApp("/static")),
                get("/static/{file}")
                        .param(String.class)
                        .handle(serve1),
                get("/static/{path1}/{file}")
                        .param(String.class)
                        .param(String.class)
                        .handle(serve2),
                get("/static/{path1}/{path2}/{file}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(serve3),
                get("/static/{path1}/{path2}/{path3}/{file}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(serve4)
        );
    }

    private Completes<Response> redirectToApp(String path) {
        return Completes.withSuccess(
                Response.of(MovedPermanently, Header.Headers.of(
                        ResponseHeader.of("Location", path),
                        ResponseHeader.of(ContentLength, 0)), ""));
    }

    private Completes<Response> serve(final String... pathSegments) {
        if (pathSegments.length == 0 || pathSegments[pathSegments.length - 1].split("\\.").length == 1)
            return serve(Stream.concat(Stream.of(pathSegments), Stream.of("index.html")).toArray(String[]::new));

        String path = pathFrom(pathSegments);
        try {
            byte[] content;
            try {
                content = staticFileCache.get(path);
            } catch (ExecutionException ex) {
                throw ex.getCause();
            }
            return Completes.withSuccess(
                    Response.of(Ok,
                            Header.Headers.of(
                                    ResponseHeader.of(ContentType, guessContentType(path)),
                                    ResponseHeader.of(ContentLength, content.length)),
                            Body.bytesToUTF8(content)
                    )
            );
        } catch (FileNotFoundException e) {
            return Completes.withSuccess(Response.of(NotFound, path + " not found."));
        } catch (Throwable e) {
            return Completes.withSuccess(Response.of(InternalServerError));
        }
    }

    private String guessContentType(final String path) {
        MimetypesFileTypeMap m = new MimetypesFileTypeMap();
        String contentType = m.getContentType(Paths.get(path).toFile());
        return (contentType != null) ? contentType : "application/octet-stream";
    }

    private String pathFrom(final String[] pathSegments) {
        // TODO: Make the static resource path configurable
        // TODO: Make the static file serving location configurable
        return Stream.of(pathSegments)
                .map(p -> p.startsWith("/") ? p.substring(1) : p)
                .map(p -> p.endsWith("/") ? p.substring(0, p.length() - 1) : p)
                .collect(Collectors.joining("/", "static/", ""));
    }

    private byte[] readFileFromClasspath(final String path) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        if (is == null)
            throw new FileNotFoundException();

        return read(is);
    }

    private static byte[] read(final InputStream is) throws IOException {
        byte[] readBytes;

        byte[] buffer = new byte[4096];

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            readBytes = baos.toByteArray();
        }
        return readBytes;
    }

    private void onRemoval(RemovalNotification<Object, Object> notification) {
        staticFileCache.invalidate(notification.getKey());
    }
}