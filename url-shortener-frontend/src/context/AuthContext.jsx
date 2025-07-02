// src/context/AuthContext.jsx
import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState("");
  const [loading, setLoading] = useState(true); // ✅ New loading flag

  // Function to fetch and update auth state
  const fetchAuthStatus = async () => {
    try {
      const res = await fetch("http://localhost:8080/authh/validate", {
        method: "GET",
        credentials: "include",
      });

      if (res.ok) {
        const name = await res.text();
        setIsAuthenticated(true);
        setUsername(name);
      } else {
        setIsAuthenticated(false);
        setUsername("");
      }
    } catch {
      setIsAuthenticated(false);
      setUsername("");
    } finally {
      setLoading(false); // ✅ Done checking auth
    }
  };

  // Call it once when app loads
  useEffect(() => {
    fetchAuthStatus();
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, loading, fetchAuthStatus }}>
      {children}
    </AuthContext.Provider>
  );
};
