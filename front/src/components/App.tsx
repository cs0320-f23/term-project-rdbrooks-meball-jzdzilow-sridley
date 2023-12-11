import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Landing from "./Landing";
import LoginPage from "./LoginPage";
import FailedLogin from "./FailedLogin";
import RoleSelection from "./RoleSelection";
import Dashboard from "./Dashboard";
import IssueTypeSelection from "./IssueTypeSelection";

// creating different components of App
const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/*" element={<Landing />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/failed-login" element={<FailedLogin />} />
        <Route path="/role-selection" element={<RoleSelection />} />
        <Route path="/issue-type-selection" element={<IssueTypeSelection />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/issue-type-selection" element={<IssueTypeSelection />} />
      </Routes>
    </Router>
  );
};

export default App;
