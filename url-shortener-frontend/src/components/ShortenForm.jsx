// src/components/ShortenForm.jsx
import React, { useState } from 'react';
import { shortenUrl } from '../api/shortenApi';
import { useNavigate } from 'react-router-dom';

const ShortenForm = ({ onResult }) => {
  const [originalUrl, setOriginalUrl] = useState('');
  const [error, setError] = useState('');
  const [shortenedResult, setShortenedResult] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const result = await shortenUrl(originalUrl);
      console.log("the result:-",result);
      setShortenedResult(result); // 👈 save in state
      onResult(result); // callback to display shortened URL
    } catch (err) {
      setError(err.message || 'Failed to shorten URL');
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-8 col-lg-6">
          <div className="card shadow-sm">
            <div className="card-body">
              <h3 className="card-title text-center mb-4">🔗 URL Shortener</h3>

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <input
                    type="url"
                    className="form-control"
                    placeholder="Enter your long URL here..."
                    value={originalUrl}
                    onChange={(e) => setOriginalUrl(e.target.value)}
                    required
                  />
                </div>

                <div className="d-flex justify-content-between">
                  <button type="submit" className="btn btn-primary w-50 me-2">
                    Shorten URL
                  </button>


                  <button
                    type="button"
                    className="btn btn-dark w-50"
                    disabled={!shortenedResult} // 👈 disable until result exists
                    onClick={() => {
                      console.log('Short code:', shortenedResult?.shortUrl); // ✅ print to console
                      navigate(`/analytics?code=${shortenedResult?.shortUrl}`);
                    }}
                  >
                    View Analytics
                  </button>
                </div>

                {error && (
                  <div className="alert alert-danger mt-3 mb-0" role="alert">
                    {error}
                  </div>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShortenForm;
