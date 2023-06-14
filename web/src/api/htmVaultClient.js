import axios from "axios";
import BindingClass from "../util/bindingClass";
import Authenticator from "./authenticator";

/**
 * Client to call the HTMVaultService.
 *
 * This could be a great place to explore Mixins. Currently the client is being loaded multiple times on each page,
 * which we could avoid using inheritance or Mixins.
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes#Mix-ins
 * https://javascript.info/mixins
  */
export default class HTMVaultClient extends BindingClass {

    constructor(props = {}) {
        super();

        //const methodsToBind = ['clientLoaded', 'getIdentity', 'login', 'logout', 'getPlaylist', 'getPlaylistSongs', 'createPlaylist', 'addDevice'];
        const methodsToBind = ['clientLoaded', 'getIdentity', 'login', 'logout', 'addDevice', 'getDevice', 'getDeviceWorkOrders', 'retireDevice', 'reactivateDevice', 'updateDevice', 'createWorkOrder', 'getWorkOrder', 'updateWorkOrder'];
        this.bindClassMethods(methodsToBind, this);

        this.authenticator = new Authenticator();;
        this.props = props;

        axios.defaults.baseURL = process.env.API_BASE_URL;
        this.axiosClient = axios;
        this.clientLoaded();
    }

    /**
     * Run any functions that are supposed to be called once the client has loaded successfully.
     */
    clientLoaded() {
        if (this.props.hasOwnProperty("onReady")) {
            this.props.onReady(this);
        }
    }

    /**
     * Get the identity of the current user
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The user information for the current user.
     */
    async getIdentity(errorCallback) {
        try {
            const isLoggedIn = await this.authenticator.isUserLoggedIn();

            if (!isLoggedIn) {
                return undefined;
            }

            return await this.authenticator.getCurrentUserInfo();
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async login() {
        this.authenticator.login();
    }

    async logout() {
        this.authenticator.logout();
    }

    async getTokenOrThrow(unauthenticatedErrorMessage) {
        const isLoggedIn = await this.authenticator.isUserLoggedIn();
        if (!isLoggedIn) {
            throw new Error(unauthenticatedErrorMessage);
        }

        return await this.authenticator.getUserToken();
    }

    async updateWorkOrder(workOrderId, workOrderType, workOrderAwaitStatus, problemReported, problemFound, summary, completionDateTime, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can update work orders.");
            const response = await this.axiosClient.put(`workOrders/${workOrderId}`, {
                workOrderId: workOrderId,
                workOrderType: workOrderType,
                workOrderAwaitStatus: workOrderAwaitStatus,
                problemReported: problemReported,
                problemFound: problemFound,
                summary: summary,
                completionDateTime: completionDateTime
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.workOrder;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async updateDevice(controlNumber, serialNumber, manufacturer, model, facilityName, assignedDepartment,
        manufactureDate, notes, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can update devices.");
            const response = await this.axiosClient.put(`devices/${controlNumber}`, {
                controlNumber: controlNumber,
                serialNumber: serialNumber,
                manufacturer: manufacturer,
                model: model,
                facilityName: facilityName,
                assignedDepartment: assignedDepartment,
                manufactureDate: manufactureDate,
                notes: notes
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.device;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async retireDevice(controlNumber, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can retire devices.");
            const response = await this.axiosClient.delete(`devices/${controlNumber}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.device;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async reactivateDevice(controlNumber, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can reactivate devices.");
            const response = await this.axiosClient.put(`devices/reactivate/${controlNumber}`, {
                controlNumber: controlNumber
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.device;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Gets the device for the given ID.
     * @param controlNumber Unique identifier for a device
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The device's metadata.
     */
    async getDevice(controlNumber, errorCallback) {
        try {
            const response = await this.axiosClient.get(`devices/${controlNumber}`);
            return response.data.device;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async getWorkOrder(workOrderId, errorCallback) {
        try {
            const response = await this.axiosClient.get(`workOrders/${workOrderId}`);
            return response.data.workOrder;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Gets the playlist for the given ID.
     * @param id Unique identifier for a playlist
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The playlist's metadata.
     */
    async getPlaylist(id, errorCallback) {
        try {
            const response = await this.axiosClient.get(`playlists/${id}`);
            return response.data.playlist;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async createWorkOrder(controlNumber, workOrderType, problemReported, problemFound, order, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can add devices.");
            const response = await this.axiosClient.post(`workOrders`, {
                controlNumber: controlNumber,
                workOrderType: workOrderType,
                problemReported: problemReported,
                problemFound: problemFound,
                sortOrder: order
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.workOrders;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    async getDeviceWorkOrders(controlNumber, order, errorCallback) {
        try {
            const response = await this.axiosClient.get(`devices/${controlNumber}/workOrders?order=${order}`);
            return response.data.workOrders;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Get the songs on a given playlist by the playlist's identifier.
     * @param id Unique identifier for a playlist
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The list of songs on a playlist.
     */
    async getPlaylistSongs(id, errorCallback) {
        try {
            const response = await this.axiosClient.get(`playlists/${id}/songs`);
            return response.data.songList;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Add a new device to the inventory.
     * @param serialNumber The serial number of the device.
     * @param manufacturer The manufacturer of the device.
     * @param model The model of the device.
     * @param facilityName The facility where this device is located.
     * @param assignedDepartment The department within the facility to which this device is assigned.
     * @param manufactureDate The date of manufacture of this device.
     * @param notes Pertinent information on the device not otherwise stored in an attribute.
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The device that has been added to the inventory.
     */
    async addDevice(serialNumber, manufacturer, model, facilityName, assignedDepartment,
        manufactureDate, notes, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can add devices.");
            const response = await this.axiosClient.post(`devices`, {
                serialNumber: serialNumber,
                manufacturer: manufacturer,
                model: model,
                facilityName: facilityName,
                assignedDepartment: assignedDepartment,
                manufactureDate: manufactureDate,
                notes: notes
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.device;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Add a song to a playlist.
     * @param id The id of the playlist to add a song to.
     * @param asin The asin that uniquely identifies the album.
     * @param trackNumber The track number of the song on the album.
     * @returns The list of songs on a playlist.
     */
    async addSongToPlaylist(id, asin, trackNumber, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can add a song to a playlist.");
            const response = await this.axiosClient.post(`playlists/${id}/songs`, {
                id: id,
                asin: asin,
                trackNumber: trackNumber
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.songList;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Search for a device.
     * @param criteria A string containing search criteria to pass to the API.
     * @returns The devices that match the search criteria.
     */
    async search(criteria, errorCallback) {
        try {
            const queryParams = new URLSearchParams({ q: criteria })
            const queryString = queryParams.toString();

            const response = await this.axiosClient.get(`devices/search?${queryString}`);

            return response.data.devices;
        } catch (error) {
            this.handleError(error, errorCallback)
        }

    }

    /**
     * Helper method to log the error and run any error functions.
     * @param error The error received from the server.
     * @param errorCallback (Optional) A function to execute if the call fails.
     */
    handleError(error, errorCallback) {
        console.error(error);

        const errorFromApi = error?.response?.data?.error_message;
        if (errorFromApi) {
            console.error(errorFromApi)
            error.message = errorFromApi;
        }

        if (errorCallback) {
            errorCallback(error);
        }
    }
}
