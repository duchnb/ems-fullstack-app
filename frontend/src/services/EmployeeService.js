import axios from "axios";


const EMPLOYEE_API_BASE_URL = "http://localhost:8080/api/employees";

export const listEmployees = () => axios.get(EMPLOYEE_API_BASE_URL);

export const createEmployee = (employee) => axios.post(EMPLOYEE_API_BASE_URL, employee);

export const getEmployee = (id) => axios.get(`${EMPLOYEE_API_BASE_URL}/${id}`);

export const updateEmployee = (id, employee) => axios.put(`${EMPLOYEE_API_BASE_URL}/${id}`, employee);

export const deleteEmployee = (id) => axios.delete(`${EMPLOYEE_API_BASE_URL}/${id}`);
