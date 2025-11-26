import React from 'react'
import { useState, useEffect } from 'react'
import {listDepartments, deleteDepartment} from '../services/DepartmentServices.js'
import {useNavigate} from 'react-router-dom'

const ListDepartmentComponents = () => {

    useEffect(() => {
            listDepartments().then((response) => {
                setDepartments(response.data)
            }).catch((error) => {console.error(error)})
        }
        , []);

    const [departments, setDepartments] = useState([]);
    const navigate = useNavigate();


    function addNewDepartment() {
        navigate('/add-department');
    }

    function updateDepartment(id) {
        navigate(`/update-department/${id}`);
    }

    function removeDepartment(id) {
        if(window.confirm('Are you sure you want to delete this department?')){
            deleteDepartment(id)
                .then(() => {
                    setDepartments(prev => (Array.isArray(prev) ? prev.filter(emp => emp.id !== id) : []));
                })
                .catch((error) => { console.error(error) })
        } else {
            return;
        }
    }

    return (
        <div className="container">
            <h2 className="text-center">List of Departments</h2>
            <button className="btn btn-primary mb-3" onClick={addNewDepartment}>Add Department</button>
            <div>
                <table className="table table-striped table-bordered table-hover table-sm">
                    <thead className="table-dark">
                    <tr>
                        <th>Department ID</th>
                        <th>Department Name</th>
                        <th>Department Description</th>
                        <th className={"text-center"}>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        departments.map(
                            Department => (
                                <tr key={Department.id}>
                                    <td>{Department.id}</td>
                                    <td>{Department.departmentName}</td>
                                    <td>{Department.departmentDescription}</td>
                                    <td className="d-flex justify-content-evenly">
                                        <button className="btn btn-info btn-sm" onClick={()=>updateDepartment(Department.id)}>Update</button>
                                        <button className="btn btn-danger btn-sm" onClick={()=>removeDepartment(Department.id)}>Delete</button>
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


export default ListDepartmentComponents
