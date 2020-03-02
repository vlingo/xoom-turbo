var flowClient = {
    get: function (name, callback) {
        appDispatcher.handle(generate("GET", null, "/flows/" + name), callback);
    }
};

var renderProcessorGraph = function (callback, data) {

    $('.page-title').text("Vlingo Step Flow: " + data.flow);

    var g = new dagreD3.graphlib.Graph().setGraph({
        nodesep: 80,
        ranksep: 20,
        edgesep: 80,
        rankdir: "TB",
        ranker: 'network-simplex'
    });

    g.graph().marginx = 80;
    g.graph().marginy = 20;

    data.nodes.forEach(function (node) {
        g.setNode(node, {
            label: node,
            class: node.toLowerCase()
        })
    });

    g.nodes().forEach(function (v) {
        var node = g.node(v);
        node.width = 250;
        node.height = 50;
        node.rx = node.ry = 5;
    });

    data.edges.forEach(function (edge) {
        g.setEdge(edge.source, edge.target, {
            label: edge.label,
            style: "stroke-width: 1.25em;",
            labelpos: edge.port % 2 ? 'r' : 'l',
            labeloffset: 10,
            minlen: ((edge.port % 2) * edge.port) + 1
        });
    });

    var render = new dagreD3.render();

    callback();

    $("#svg-canvas").toggleClass("active");

    var svg = d3.select("svg"),
        svgGroup = svg.append("g"),
        inner = svg.select("g"),
        zoom = d3.behavior.zoom().on("zoom", function () {
            inner.attr("transform", "translate(" + d3.event.translate + ")" + "scale(" + d3.event.scale + ")");
        });

    render(d3.select("svg g"), g);
    inner.call(render, g);

    var draw = function (isUpdate) {
        var graphWidth = g.graph().width;
        var graphHeight = g.graph().height;
        var width = parseInt(svg.style("width").replace(/px/, ""));
        var height = parseInt(svg.style("height").replace(/px/, ""));
        var zoomScale = Math.min(width / graphWidth, height / graphHeight);
        var translate = [(width / 2) - ((graphWidth * zoomScale) / 2), (height / 2) - ((graphHeight * zoomScale) / 2)];
        zoom.translate(translate);
        zoom.scale(zoomScale);
        zoom.event(isUpdate ? svg.transition().duration(300) : d3.select("svg"));
    };

    // Detect window changes and redraw the diagram to fit within its bounded container
    setInterval(function () {
        draw(true);
    }, 100);

    draw();
};