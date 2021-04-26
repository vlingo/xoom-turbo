import {useCallback, useEffect, useState} from "react";
import axios from "axios";
import FormModal from "../FormModal";
import {Link} from "react-router-dom";
import useFormHandler from "../../utils/FormHandler";
import LoadingOrFailed from "../LoadingOrFailed";

const EMPTY_FORM = {
  <#list aggregate.stateFields as field>
    ${field.name}: ''<#if field?has_next>,</#if>
  </#list>
};

const ${fns.capitalize(fns.makePlural(aggregate.aggregateName))} = () => {

  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState([]);
  const [form, onFormValueChange, resetForm] = useFormHandler(EMPTY_FORM);
  const [modalOpen, setModalOpen] = useState(false);

  const loadItems = useCallback(() => {
    axios.get('${aggregate.api.rootPath}')
      .then(res => res.data)
      .then(data => {
        console.log('${aggregate.aggregateName} axios success', data);
        setItems(data);
      })
      .catch((e) => {
        console.error('${aggregate.aggregateName} axios failed', e);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const onFormSubmit = useCallback((e) => {
    console.log('form submitted', form);

    axios.${creatorRoute.httpMethod?lower_case}('${aggregate.api.rootPath}', form)
    .then(res => res.data)
    .then(data => {
      console.log('${aggregate.aggregateName} axios success', data);
      loadItems();
      setModalOpen(false);
      resetForm();
    })
    .catch(e => {
      console.error('${aggregate.aggregateName} axios failed', e);
    });
  }, [form, resetForm, loadItems]);

  const openModal = useCallback(() => {
    setModalOpen(true);
  }, []);

  const closeModal = useCallback(() => {
    setModalOpen(false);
  }, []);

  useEffect(() => {
    setLoading(true);
    loadItems();
  }, [loadItems]);

  return (
    <>
      <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 className="h2">${fns.makePlural(aggregate.aggregateName)}</h1>
        <div className="btn-toolbar mb-2 mb-md-0">
          <div className="btn-group me-2">
            <button type="button" className="btn btn-sm btn-outline-secondary" onClick={openModal}>${fns.capitalize(creatorMethod.name)}</button>
          </div>
        </div>
      </div>
      <div>
        {
        items ?
        <table className={'table table-striped table-bordered'}>
        <thead>
          <tr>
          <#list aggregate.stateFields as field>
            <th>${fns.capitalize(field.name)}</th>
          </#list>
          </tr>
        </thead>
        <tbody>
        {items.map(item => (
        <tr key={item.id}>
          <#list aggregate.stateFields as field>
              <#if field_index == 0>
                <td>
                  <Link to={"${aggregate.api.rootPath}/"+item.id}>{item.${field.name}}</Link>
                </td>
              <#else>
                  <td>{item.${field.name}}</td>
              </#if>
          </#list>
        </tr>
        ))}
        </tbody>
        </table>
        : <LoadingOrFailed loading={loading}/>
      }
      </div>
      <FormModal title={'${fns.capitalize(creatorMethod.name)}'} show={modalOpen} close={closeModal} submit={onFormSubmit}>
        <#list creatorMethod.parameters as p>
          <div className='mb-3'>
            <label htmlFor='${p}' className={'form-label text-capitalize'}>${fns.capitalize(p)}</label>
            <input id='name' name={'${p}'} required={true} value={form.${p}} onChange={onFormValueChange} className={'form-control form-control-sm'}/>
          </div>
        </#list>
      </FormModal>
    </>
  );
};

export default ${fns.capitalize(fns.makePlural(aggregate.aggregateName))};
