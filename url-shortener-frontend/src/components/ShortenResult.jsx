// src/components/ShortenResult.jsx
import React from 'react';

const ShortenResult = ({ shortCode }) => {
  // The full URL is constructed so that when clicked, it goes via the gateway's redirect endpoint
  const redirectUrl = `http://localhost:8080/redirect/${shortCode}`;

  return (
    <div className="result-container">
      <p>Your shortened URL:</p>
      <a href={redirectUrl} target="_blank" rel="noopener noreferrer">
        {redirectUrl}
      </a>
    </div>
  );
};

export default ShortenResult;
