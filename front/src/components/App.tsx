// src/AppRouter.tsx
import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Landing from "./Landing";
import LoginPage from "./LoginPage";
import FailedLogin from "./FailedLogin";
import RoleSelection from "./RoleSelection";
import Dashboard from "./Dashboard";

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/*" element={<Landing />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/failed-login" element={<FailedLogin />} />
        <Route path="/role-selection" element={<RoleSelection />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Router>
  );
};

export default App;
