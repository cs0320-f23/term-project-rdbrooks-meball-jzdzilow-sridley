import React from "react";
import { useNavigate } from "react-router-dom";

import "../styles/LoginPage.css";

const FailedLogin = () => {
  const navigate = useNavigate();

  return (
    <div className="login-body">
      <div className="login-container">
        <h1 style={{ color: "darkred" }}>Login Failed</h1>
        <p>Your email or password is incorrect. Please try again.</p>
        <button onClick={() => navigate("/login")} className="btn">
          Back to Login
        </button>
      </div>
    </div>
  );
};

export default FailedLogin;
