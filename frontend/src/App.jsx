// src/App.jsx

import './App.css';
import HeaderComponent from "./components/HeaderComponent";
import ListEmployeeComponents from "./components/ListEmployeeComponents";
import FooterComponent from "./components/FooterComponent";
import { BrowserRouter, Routes, Route } from "react-router-dom"
import EmployeeComponents from "./components/EmployeeComponents";
import ListDepartmentComponents from "./components/ListDepartmentComponents";

function App() {

    return (
        <>
        <BrowserRouter>
            <HeaderComponent/>
            <Routes>
                <Route>
                    {/*http://localhost:5173/*/}
                    <Route path="/" element={<ListEmployeeComponents/>}/>
                    {/*http://localhost:5173/employees*/}
                    <Route path="/employees" element={<ListEmployeeComponents/>}/>
                    {/*http://localhost:5173/add-employee*/}
                    <Route path="/add-employee" element={<EmployeeComponents/>}/>
                    {/*http://localhost:5173/edit-employee/1*/}
                    <Route path="/update-employee/:id" element={<EmployeeComponents/>}/>
                    {/*http://localhost:5173/departments*/}
                    <Route path="/departments" element={<ListDepartmentComponents/>}/>

                </Route>
            </Routes>
            <FooterComponent/>
        </BrowserRouter>
        </>
    );
}

export default App;