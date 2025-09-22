// src/api/client.js
import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', //Spring Boot backend URL
    headers: {
        'Content-Type': 'application/json',
    },
});

export default apiClient;