import React from 'react'
import { useState } from 'react'

const ListDepartmentComponents = () => {

    let dummyData = [
        {
            id: 1,
            departmentName: "IT",
            departmentDescription: "IT Department"
        },
        {
            id: 2,
            departmentName: "HR",
            departmentDescription: "HR Department"
        },
        {
            id: 3,
            departmentName: "Finance",
            departmentDescription: "Finance Department"
        },
        {
            id: 4,
            departmentName: "Marketing",
            departmentDescription: "Marketing Department"
        },
        {
            id: 5,
            departmentName: "Sales",
            departmentDescription: "Sales Department"
        },
        {
            id: 6,
            departmentName: "R&D",
            departmentDescription: "Research and Development Department"
        }

    ];

    const [departments, setDepartments] = useState(dummyData);

    return (
        <div>ListDepartmentComponent</div>
    )
}
export default ListDepartmentComponents
