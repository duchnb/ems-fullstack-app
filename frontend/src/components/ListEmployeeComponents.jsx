import React from 'react'
import { useEffect, useState } from 'react'
import { listEmployees, deleteEmployee } from '../services/EmployeeService'
import {useNavigate} from 'react-router-dom'

const ListEmployeeComponents = () => {

    const [employees, setEmployees] = useState([])

    const navigate = useNavigate();

    useEffect(() => {
            listEmployees().then((response) => {
                setEmployees(response.data)
            }).catch((error) => {console.error(error)})
        }
        , []);

    const addNewEmployee = () => {
        navigate('/add-employee');
    }

    function updateEmployee(id){
        navigate(`/update-employee/${id}`);
    }

    function removeEmployee(id){
        if(window.confirm('Are you sure you want to delete this employee?')){
            deleteEmployee(id)
                .then(() => {
                    setEmployees(prev => (Array.isArray(prev) ? prev.filter(emp => emp.id !== id) : []));
                })
                .catch((error) => { console.error(error) })
        } else {
            return;
        }
    }

    return (
        <div className="container">
            <h2 className="text-center">List of Employees</h2>
            <button className="btn btn-primary mb-3" onClick={addNewEmployee}>Add Employee</button>
            <div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead className="table-dark">
                    <tr>
                        <th>Employee ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th className={"text-center"}>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        employees.map(
                            employee => (
                                <tr key={employee.id}>
                                    <td>{employee.id}</td>
                                    <td>{employee.firstName}</td>
                                    <td>{employee.lastName}</td>
                                    <td>{employee.email}</td>
                                    <td>{employee.departmentName || ''}</td>
                                    <td className="d-flex justify-content-evenly">
                                        <button className="btn btn-info btn-sm" onClick={()=>updateEmployee(employee.id)}>Update</button>
                                        <button className="btn btn-danger btn-sm" onClick={()=>removeEmployee(employee.id)}>Delete</button>
                                    </td>
                                </tr>
                            )
                        )
                    }
                    </tbody>
                </table>
            </div>
        </div>
    )
}
export default ListEmployeeComponents

