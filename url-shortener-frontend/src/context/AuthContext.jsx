import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState("");

  // Function to fetch and update auth state
  const fetchAuthStatus = async () => {
    try {
      const res = await fetch("http://localhost:8080/auth/validate", {
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
    }
  };

  // Call it once when app loads
  useEffect(() => {
    fetchAuthStatus();
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, fetchAuthStatus }}>
      {children}
    </AuthContext.Provider>
  );
};
