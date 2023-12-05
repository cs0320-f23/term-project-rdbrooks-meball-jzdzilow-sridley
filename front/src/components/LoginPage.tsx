import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";
import { useRecoilState, useSetRecoilState } from "recoil";
import { mockedMode, userRoleState, userSessionState, userState } from "../recoil/atoms";
import { UserRole } from "../recoil/atoms";
import { IUser } from "../types/IUser";

// Used the following video for firebase authentication: https://www.youtube.com/watch?v=vDT7EnUpEoo
// Added to import from other services
import { getAuth, GoogleAuthProvider, signInWithPopup } from "firebase/auth";
// Import functions needed from appropriate SDKs
import { initializeApp } from "firebase/app";

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyCpEp6SMcO3hen7UKMeR-NNEy7ySSWStYI",
  authDomain: "collab-section-manager.firebaseapp.com",
  projectId: "collab-section-manager",
  storageBucket: "collab-section-manager.appspot.com",
  messagingSenderId: "682750943740",
  appId: "1:682750943740:web:36bb8a15ff7bea374fd279",
  measurementId: "G-1FFWDE2HTT",
};

function getRoleFromBackend(email: string): Promise<string> {
  return fetch("http://localhost:3333/isTA?email=" + email)
    .then((response) => response.json())
    .then((data) => {
      return data["message"];
    })
    .catch((e) => {
      return "ERROR: " + e;
    });
}

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const setUserSession = useSetRecoilState(userSessionState);
  //const setUser = useSetRecoilState(userState);
  //const [userRole, setUserRole] = useRecoilState(userRoleState);

  const handleLoginMocked = async () => {
    const response = await fetch("http://localhost:2000/login/");
    const data = await response.json();
    const users: IUser[] = data.users;

    const user = users.find((u) => u.email === email);
    console.log(email);

    if (user) {
      //setUser(user);
      if (user.role === "student") {
        // pass user information to the further component
        setUserSession({user: user, role: UserRole.NoneSelected, time: null});
        return navigate("/role-selection");
      } else if (user.role === "instructor") {
        setUserSession({user: user, role: UserRole.Instructor, time: null});
        //setUserRole({ role: UserRole.Instructor, time: null });
        return navigate("/dashboard");
      }
    } else {
      // Mocking login failure
      return navigate("/failed-login");
    }
  };

  // Initialize Firebase login and redirect to appropriate page
  const app = initializeApp(firebaseConfig);
  const auth = getAuth(app);
  const provider = new GoogleAuthProvider();

  const signInWithGoogle = async () => {
    try {
      const result = await signInWithPopup(auth, provider);

      const name: string | null = result.user.displayName;
      const email: string | null = result.user.email;
      console.log(name);
      console.log(email);

      if (name == null || email == null) {
        console.log("error: name of email is null");
      } else {
        let user: IUser = {
          email: email,
          name: name,
          role: "",
        };

        if (!user?.email.includes("brown.edu")) {
          return navigate("/failed-login");
        }

        try {
          const roleFromBackend = await getRoleFromBackend(user.email);
          user.role = roleFromBackend;
        } catch (error) {
          console.error("Error fetching role from backend:", error);
          // Determine what to do with the error, handle it as needed
          return navigate("/failed-login");
        }

        //setUser(user);
        if (user.role === "student") {
          setUserSession({user: user, role: UserRole.NoneSelected, time: null});
          return navigate("/role-selection");
        } else if (user.role === "instructor") {
          setUserSession({user: user, role: UserRole.Instructor, time: null});
          //setUserRole({ role: UserRole.Instructor, time: null });
          return navigate("/dashboard");
        }
      }
    } catch (error) {
      console.log(error);
    }
  };

  if (!mockedMode) {
    return (
      <div className="login-body">
        <div className="login-container">
          <h1>Welcome to Debugging Helper</h1>
          <button onClick={signInWithGoogle} className="btn">
            Sign In With Google
          </button>
        </div>
      </div>
    );
  } else {
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
          <button onClick={handleLoginMocked} className="btn">
            Login
          </button>
        </div>
      </div>
    );
  }
};

export default LoginPage;
