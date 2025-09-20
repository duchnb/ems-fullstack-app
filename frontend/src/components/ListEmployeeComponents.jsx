import React from 'react'
import { useEffect, useState } from 'react'
import { listEmployees } from '../services/employeeService'
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
    return (
        <div className="container">
            <h2 className="text-center">List of Employees</h2>
            <button className="btn btn-primary mb-3" onClick={addNewEmployee}>Add Employee</button>
            <div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead className="table-dark">
                        <tr>
                            <th>Employee ID</th>
                            <th>Employee First Name</th>
                            <th>Employee Last Name</th>
                            <th>Employee Email</th>
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

