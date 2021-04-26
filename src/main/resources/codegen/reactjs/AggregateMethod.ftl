import FormModal from "../FormModal";
import useFormHandler from "../../utils/FormHandler";
import {useCallback} from "react";
import axios from "axios";

const ${fns.capitalize(aggregate.aggregateName)}${fns.capitalize(method.name)} = ({defaultForm, complete}) => {

  const [form, onFormValueChange] = useFormHandler(defaultForm);

  const submit = useCallback((e) => {
    axios.${route.httpMethod?lower_case}('${aggregate.api.rootPath}/'+form.id+'${route.path}', form)
    .then(res => res.data)
    .then(data => {
      complete(data);
      console.log('${aggregate.aggregateName}${fns.capitalize(method.name)} axios complete', data);
    })
    .catch(e => {
      console.error('${aggregate.aggregateName}${fns.capitalize(method.name)} axios failed', e);
    });
  }, [form, complete]);

  const close = useCallback((e) => {
    complete();
  }, [complete]);

  return (
    <>
      <FormModal title={"${method.name}"} show={true} close={close} submit={submit}>
        <#list method.parameters as p>
          <div className='mb-3'>
            <label htmlFor='${p}' className={'form-label text-capitalize'}>${fns.capitalize(p)}</label>
            <input id='${p}' name={'${p}'} required={true} value={form.${p}} onChange={onFormValueChange} className={'form-control form-control-sm'}/>
          </div>
        </#list>
      </FormModal>
    </>
  )
};

export default ${fns.capitalize(aggregate.aggregateName)}${fns.capitalize(method.name)};
