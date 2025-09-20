// src/App.jsx

import './App.css';
import HeaderComponent from "./components/HeaderComponent";
import ListEmployeeComponents from "./components/ListEmployeeComponents";
import FooterComponent from "./components/FooterComponent";
import { BrowserRouter, Routes, Route } from "react-router-dom"

function App() {

    return (
        <>
        <BrowserRouter>
            <HeaderComponent/>
            <Routes>
                <Route>
                    <Route path="/" element={<ListEmployeeComponents/>}/>
                    <Route path="/employees" element={<ListEmployeeComponents/>}/>
                </Route>
            </Routes>
            <FooterComponent/>
        </BrowserRouter>
        </>
    );
}

export default App;