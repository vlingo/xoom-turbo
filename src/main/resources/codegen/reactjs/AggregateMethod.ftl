import FormModal from "../FormModal";
import useFormHandler from "../../utils/FormHandler";
import {useCallback} from "react";
import axios from "axios";
<#macro printFormElement name type>
    <#if valueTypes[type]??>
        <#list valueTypes[type] as subType>
            <@printFormElement "${name}.${subType.name}" subType.type/>
        </#list>
    <#else>
      <div className='mb-3'>
        <label htmlFor='${name}' className={'form-label text-capitalize'}>${fns.capitalizeMultiWord(name?replace('.', ' '))}</label>
        <input id='${name}' name={'${name}'} required={true} value={form.${name}} onChange={onFormValueChange} className={'form-control form-control-sm'}/>
      </div>
    </#if>
</#macro>

const ${fns.capitalize(aggregate.aggregateName)}${fns.capitalize(method.name)} = ({defaultForm, complete}) => {

  const [form, onFormValueChange] = useFormHandler(defaultForm);

  const submit = useCallback((e) => {
    axios.${route.httpMethod?lower_case}('${aggregate.api.rootPath}/'+form.id+'/${route.path}', form)
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
      <FormModal title={"${fns.capitalize(method.name)}"} show={true} close={close} submit={submit}>
        <#list method.parameters as p>
          <@printFormElement p fieldTypes[p] />
        </#list>
      </FormModal>
    </>
  )
};

export default ${fns.capitalize(aggregate.aggregateName)}${fns.capitalize(method.name)};
