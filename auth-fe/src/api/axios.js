import axios from "axios";
import router from '../router';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  withCredentials: true,
});

function getSessionAuth() {
  const session = sessionStorage.getItem('auth');
  if (!session) return {};
  try {
    return JSON.parse(session);
  } catch {
    return {};
  }
}

function setSessionAuth(obj) {
  sessionStorage.setItem('auth', JSON.stringify(obj));
}

function clearSessionAuth() {
  sessionStorage.removeItem('auth');
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
      const { accessToken } = getSessionAuth();
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
      const { refreshToken } = getSessionAuth();
      if (!refreshToken) {
        clearSessionAuth();
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
        const { accessToken, refreshToken: newRefreshToken, user } = res.data;
        setSessionAuth({ accessToken, refreshToken: newRefreshToken, user });
        api.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
        processQueue(null, accessToken);
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        clearSessionAuth();
        router.push('/');
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);

export { getSessionAuth, setSessionAuth, clearSessionAuth };
export default api;
