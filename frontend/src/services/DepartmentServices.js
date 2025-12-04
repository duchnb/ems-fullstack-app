import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const DEPARTMENT_API_BASE_URL = `${API_BASE_URL}/api/departments`;

export const listDepartments = () => axios.get(DEPARTMENT_API_BASE_URL);

export const createDepartment = (employee) => axios.post(DEPARTMENT_API_BASE_URL, employee);

export const getDepartment = (id) => axios.get(`${DEPARTMENT_API_BASE_URL}/${id}`);

export const updateDepartment = (id, employee) => axios.put(`${DEPARTMENT_API_BASE_URL}/${id}`, employee);

export const deleteDepartment = (id) => axios.delete(`${DEPARTMENT_API_BASE_URL}/${id}`);