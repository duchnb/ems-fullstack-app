import React from 'react'
import {NavLink} from 'react-router-dom'

const HeaderComponent = () => {
    return (
        <div>
            <header>
                <nav className="navbar navbar-expand-lg navbar-dark bg-dark shadow-lg" style={{borderBottom: '1px solid rgba(139, 92, 246, 0.6)', boxShadow: '0 2px 12px rgba(139, 92, 246, 0.25)'}}>
                    <a className="navbar-brand" href="#">Employee Management System</a>
                    <div className="collapse navbar-collapse" id="navbarNav">
                        <ul className="navbar-nav">
                            <li className="nav-item">
                                <NavLink to='/employees' className='nav-link'>Employees</NavLink>
                            </li>
                            <li className="nav-item">
                                <NavLink to='/departments' className='nav-link'>Departments</NavLink>
                            </li>

                        </ul>
                    </div>
                </nav>
            </header>
        </div>
    )
}
export default HeaderComponent
