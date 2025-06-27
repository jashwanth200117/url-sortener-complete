// src/pages/Home.jsx
import React, { useState } from 'react';
import ShortenForm from '../components/ShortenForm';
import ShortenResult from '../components/ShortenResult';

const Home = () => {
  // State to hold the shortCode from the API response
  const [shortCode, setShortCode] = useState('');

  // Callback to be passed to ShortenForm once we get a result
    const handleResult = (data) => {
      // FIX: use shortUrl instead of shortCode
      setShortCode(data.shortUrl);
    };

  return (
    <div className="home-container">
      <ShortenForm onResult={handleResult} />
      {shortCode && <ShortenResult shortCode={shortCode} />}
    </div>
  );
};

export default Home;
