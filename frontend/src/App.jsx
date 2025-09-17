// src/App.jsx
import { useState, useEffect } from 'react';
import apiClient from './api/client'; // Import our new api client
import './App.css';

function App() {
    const [message, setMessage] = useState('Loading...'); // Initial message

    // useEffect hook runs after the component mounts
    useEffect(() => {
        // Fetch the message from the backend
        apiClient.get('/hello')
            .then(response => {
                // If successful, update the message state
                setMessage(response.data);
            })
            .catch(error => {
                // If there's an error, log it and show an error message
                console.error("There was an error fetching the data!", error);
                setMessage('Failed to connect to the backend.');
            });
    }, []); // The empty array [] means this effect runs only once

    return (
        <>
            <h1>Employee Management System</h1>
            <div className="card">
                <h2>Backend Status</h2>
                <p>{message}</p>
            </div>
        </>
    );
}

export default App;