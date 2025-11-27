import React from 'react'
import {NavLink} from 'react-router-dom'

const HeaderComponent = () => {
    return (
        <div>
            <header className="ems-header">
                <div className="ems-title">Employee Management System</div>
                <nav className="ems-tabs">
                    <NavLink
                        to='/employees'
                        className={({isActive}) => `ems-tab${isActive ? ' active' : ''}`}
                    >
                        Employees
                    </NavLink>
                    <NavLink
                        to='/departments'
                        className={({isActive}) => `ems-tab${isActive ? ' active' : ''}`}
                    >
                        Departments
                    </NavLink>
                </nav>
            </header>
        </div>
    )
}
export default HeaderComponent
