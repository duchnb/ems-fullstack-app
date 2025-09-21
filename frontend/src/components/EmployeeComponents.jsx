import React from 'react'
import {useState} from 'react'
import {createEmployee} from '../services/EmployeeService'
import {useNavigate} from 'react-router-dom'

function EmployeeComponents() {

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');

    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        // Handle form submission logic here
        const employee = {firstName, lastName, email};
        createEmployee(employee).then((response) => {
            console.log(response.data);
            navigate('/employees');
        })
    }


    return (
        <div className="container">
            <div className='row'>
                <div className='col-md-6 offset-md-3 card mt-5 p-4 shadow bg-light'>
                    <h2 className='text-center'>Add Employee</h2>
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
                            <button type='submit' className='btn btn-success'>Add Employee</button>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default EmployeeComponents
