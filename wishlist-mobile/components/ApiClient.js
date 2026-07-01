import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const BASE_URL = 'http://54.234.92.98:8000';

const ApiClient = {
  async getToken() {
    return await AsyncStorage.getItem('token');
  },

  async setToken(token) {
    await AsyncStorage.setItem('token', token);
  },

  async removeToken() {
    await AsyncStorage.removeItem('token');
  },

  async post(endpoint, data) {
    try {
      const response = await axios.post(`${BASE_URL}${endpoint}`, data);
      return response.data;
    } catch (error) {
      return error.response?.data || { error: 'Connection error' };
    }
  },

  async postAuth(endpoint, data) {
    try {
      const token = await this.getToken();
      const response = await axios.post(`${BASE_URL}${endpoint}`, data, {
        headers: { authorization: `Bearer ${token}` }
      });
      return response.data;
    } catch (error) {
      return error.response?.data || { error: 'Connection error' };
    }
  },

  async getAuth(endpoint) {
    try {
      const token = await this.getToken();
      const response = await axios.get(`${BASE_URL}${endpoint}`, {
        headers: { authorization: `Bearer ${token}` }
      });
      return response.data;
    } catch (error) {
      return error.response?.data || { error: 'Connection error' };
    }
  },

  async deleteAuth(endpoint) {
    try {
      const token = await this.getToken();
      const response = await axios.delete(`${BASE_URL}${endpoint}`, {
        headers: { authorization: `Bearer ${token}` }
      });
      return response.data;
    } catch (error) {
      return error.response?.data || { error: 'Connection error' };
    }
  },

  async putAuth(endpoint, data) {
    try {
      const token = await this.getToken();
      const response = await axios.put(`${BASE_URL}${endpoint}`, data, {
        headers: { authorization: `Bearer ${token}` }
      });
      return response.data;
    } catch (error) {
      return error.response?.data || { error: 'Connection error' };
    }
  },
};

export default ApiClient;