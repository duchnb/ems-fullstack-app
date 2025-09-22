import React, { useEffect } from 'react'
import {useState} from 'react'
import {createEmployee, getEmployee, updateEmployee} from '../services/EmployeeService'
import {listDepartments} from '../services/DepartmentServices'
import {useNavigate, useParams} from 'react-router-dom'

function EmployeeComponents() {

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');

    const [departmentId, setDepartmentId] = useState('');
    const [departments, setDepartments] = useState([]);

    useEffect(() => {
        if(departments.length === 0){
            listDepartments()
                .then(res => {
                    setDepartments(res.data);
                })
                .catch(err => console.error(err));
        }
        return () => {

        }
    }, []);


    const navigate = useNavigate();

    const {id} = useParams();

    useEffect(() => {
        if(id){
            getEmployee(id)
                .then(res => {
                    const emp = res.data;
                    setFirstName(emp.firstName || '');
                    setLastName(emp.lastName || '');
                    setEmail(emp.email || '');
                    setDepartmentId(emp.departmentId || '')
                })
                .catch(err => console.error(err));
        }
    }, [id]);


    const handleSubmit = (e) => {
        e.preventDefault();

        // Ensure a department is selected
        if (!departmentId) {
            alert('Please select a department.');
            return;
        }

        const employee = { firstName, lastName, email, departmentId: Number(departmentId) };

        if (id) {
            updateEmployee(id, employee)
                .then(() => navigate('/employees'))
                .catch(err => console.error(err));
        } else {
            createEmployee(employee)
                .then(() => navigate('/employees'))
                .catch(err => console.error(err));
        }
    }

    function pageTitle(){
        if(id){
            return <h2 className='text-center'>Update Employee</h2>
        }else
            return <h2 className='text-center'>Add Employee</h2>
    }

    console.log(departments);
    return (
        <div className="container">
            <div className='row'>
                <div className='col-md-6 offset-md-3 card mt-5 p-4 shadow bg-light'>
                    {
                        pageTitle()
                    }
                    <div className='card-body'>
                        <form onSubmit={handleSubmit}>
                            <div className='form-group mb-3'>
                                <label htmlFor='firstName' className='form-label'>First Name:</label>
                                <input type='text' className='form-control' id='firstName' value={firstName}
                                       placeholder='Enter first name'
                                       onChange={(e) => setFirstName(e.target.value)} required/>
                            </div>
                            <div className='form-group mb-3'>
                                <label htmlFor='lastName' className='form-label'>Last Name:</label>
                                <input type='text' className='form-control' id='lastName' value={lastName}
                                       placeholder='Enter last name'
                                       onChange={(e) => setLastName(e.target.value)} required/>
                            </div>
                            <div className='form-group mb-3'>
                                <label htmlFor='email' className='form-label'>Email:</label>
                                <input type='email' className='form-control' id='email' value={email}
                                       placeholder='Enter email'
                                       onChange={(e) => setEmail(e.target.value)} required/>
                            </div>
                            <div className='form-group mb-3'>
                                <label htmlFor='department' className='form-label'>Department:</label>
                                <select className='form-control' id='department' value={departmentId}
                                       required
                                       onChange={(e) => setDepartmentId(e.target.value)} >
                                    <option value=''>Select Department</option>
                                    {
                                        departments.map(dept => (
                                            <option key={dept.id} value={dept.id}>{dept.departmentName}</option>
                                        ))
                                    }

                                </select>
                            </div>
                            <button type='submit' className='btn btn-success'>{id ? 'Update Employee' : 'Add Employee'}</button>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default EmployeeComponents
