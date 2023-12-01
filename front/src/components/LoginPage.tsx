import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";
import { useRecoilState, useSetRecoilState } from "recoil";
import { userRoleState, userState } from "../recoil/atoms";
import { UserRole } from "../recoil/atoms";
import { IUser } from "../types/IUser";

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [user, setUser] = useRecoilState(userState);
  const [userRole, setUserRole] = useRecoilState(userRoleState);

  useEffect(() => {
    console.log("user" + user, "user role" + userRole.role, userRole.time);
  });

  const handleLogin = async () => {
    const response = await fetch("http://localhost:2000/login/");
    const data = await response.json();
    const users: IUser[] = data.users;

    const user = users.find((u) => u.email === email);
    console.log(email);

    if (user) {
      setUser(user);
      if (user.role === "student") {
        // pass user information to the further component
        return navigate("/role-selection", { state: { user } });
      } else if (user.role === "instructor") {
        setUserRole({ role: UserRole.Instructor, time: null });
        return navigate("/dashboard");
      }
    } else {
      // Mocking login failure
      return navigate("/failed-login");
    }
  };

  return (
    <div className="login-body">
      <div className="login-container">
        <h1>Welcome to Debugging Helper</h1>
        <div className="input-box">
          <input
            type="email"
            placeholder="username"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div className="input-box">
          <input
            type="password"
            placeholder="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button onClick={handleLogin} className="btn">
          Login
        </button>
      </div>
    </div>
  );
};

export default LoginPage;
