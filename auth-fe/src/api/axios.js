import axios from "axios";
import router from '../router';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  withCredentials: true,
});

function getSessionTokens() {
  const session = sessionStorage.getItem('auth_session');
  if (!session) return {};
  try {
    return JSON.parse(session);
  } catch {
    return {};
  }
}

function setSessionTokens(obj) {
  sessionStorage.setItem('auth_session', JSON.stringify(obj));
}

function clearSession() {
  sessionStorage.removeItem('auth_session');
}

let isRefreshing = false;
let failedQueue = [];

function processQueue(error, token = null) {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
}

api.interceptors.request.use(
  (config) => {
    if (!config.skipAuth) {
      const { accessToken } = getSessionTokens();
      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 401 && !originalRequest._retry && !originalRequest.url.endsWith('/auth/login')) {
      const { refreshToken } = getSessionTokens();
      if (!refreshToken) {
        clearSession();
        router.push('/');
        return Promise.reject(error);
      }
      if (isRefreshing) {
        return new Promise(function(resolve, reject) {
          failedQueue.push({resolve, reject});
        })
        .then(token => {
          originalRequest.headers.Authorization = 'Bearer ' + token;
          return api(originalRequest);
        })
        .catch(err => Promise.reject(err));
      }
      originalRequest._retry = true;
      isRefreshing = true;
      try {
        const res = await api.post('/auth/refresh', { refreshToken });
        const { accessToken, refreshToken: newRefreshToken, ...rest } = res.data;
        setSessionTokens({ accessToken, refreshToken: newRefreshToken, ...rest });
        api.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
        processQueue(null, accessToken);
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        clearSession();
        router.push('/');
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);

export { getSessionTokens, setSessionTokens, clearSession };
export default api;
