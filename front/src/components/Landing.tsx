import React, { useEffect, useState } from "react";
import logo from "../assets/logos/brownlogo.png";
import "../styles/Landing.css";
import { useNavigate } from "react-router-dom";

function Landing() {
  const navigate = useNavigate();
  const [shouldTransition, setShouldTransition] = useState(false);

  useEffect(() => {
    // first timer for the pulsing "data fetch" of 2.5s
    const timer = setTimeout(() => {
      setShouldTransition(true);
      // second timer for navigating to the login after 0.5s - time of scaling
      setTimeout(() => {
        navigate("/login");
      }, 500); // Adjust the delay as needed
    }, 2500);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className={`landing ${shouldTransition ? "transition" : ""}`}>
      <header className="landing-header">
        <img src={logo} className="pulsing-logo" alt="logo" />
      </header>
    </div>
  );
}

export default Landing;
