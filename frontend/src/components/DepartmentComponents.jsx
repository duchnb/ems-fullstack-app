import React, { useEffect } from 'react'
import {useState} from 'react'
import {createDepartment, getDepartment, updateDepartment} from '../services/DepartmentServices.js'
import {useNavigate, useParams} from 'react-router-dom'

function DepartmentComponents() {

    const [departmentName, setDepartmentName] = useState('');
    const [departmentDescription, setDepartmentDescription] = useState('');


    const navigate = useNavigate();

    const {id} = useParams();

    useEffect(() => {
        if(id){
            getDepartment(id)
                .then(res => {
                    const emp = res.data;
                    setDepartmentName(emp.departmentName || '');
                    setDepartmentDescription(emp.departmentDescription || '');

                })
                .catch(err => console.error(err));
        }
    }, [id]);


    const handleSubmit = (e) => {
        e.preventDefault();
        const department = {departmentName, departmentDescription};
        if(id){
            updateDepartment(id, department)
                .then(() => navigate('/departments'))
                .catch(err => console.error(err));
        } else {
            createDepartment(department)
                .then(() => navigate('/departments'))
                .catch(err => console.error(err));
        }
    }

    function pageTitle(){
        if(id){
            return <h2 className='text-center'>Update Department</h2>
        }else
            return <h2 className='text-center'>Add Department</h2>
    }
    return (
        <div className="container">
            <div className='row'>
                <div className='col-md-6 offset-md-3 card mt-5 p-4 shadow'>
                    {
                        pageTitle()
                    }
                    <div className='card-body'>
                        <form onSubmit={handleSubmit}>
                            <div className='form-group mb-3'>
                                <label htmlFor='departmentName' className='form-label'>Department Name:</label>
                                <input type='text' className='form-control' id='departmentName' value={departmentName}
                                       placeholder='Enter department name'
                                       onChange={(e) => setDepartmentName(e.target.value)} required/>
                            </div>
                            <div className='form-group mb-3'>
                                <label htmlFor='departmentDescription' className='form-label'>Department Description:</label>
                                <input type='text' className='form-control' id='departmentDescription' value={departmentDescription}
                                       placeholder='Enter description'
                                       onChange={(e) => setDepartmentDescription(e.target.value)} required/>
                            </div>

                            <button type='submit' className='btn btn-success'>{id ? 'Update Department' : 'Add Department'}</button>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default DepartmentComponents
