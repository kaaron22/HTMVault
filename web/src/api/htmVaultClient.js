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

        const methodsToBind = ['clientLoaded', 'getIdentity', 'login', 'logout', 'addDevice', 'getDevice', 'getDeviceWorkOrders', 'retireDevice', 'reactivateDevice', 'updateDevice',
         'createWorkOrder', 'getWorkOrder', 'updateWorkOrder', 'closeWorkOrder', 'getManufacturersAndModels', 'getFacilitiesAndDepartments'];
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

    /**
     * Authenticated method to close a work order if the work order has been filled in completely
     * @param {*} workOrderId the id of the work order
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the updated work order's metadata, if successful in closing it
     */
    async closeWorkOrder(workOrderId, errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can close work orders.");
            const response = await this.axiosClient.delete(`workOrders/${workOrderId}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.workOrder;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Authenticated method to get full list of manufacturers with their associated lists of models
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the list's metadata if successful in obtaining it from the database, including an empty list if applicable
     */
    async getManufacturersAndModels(errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can get lists of manufacturers and models.");
            const response = await this.axiosClient.get(`manufacturerModels`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.manufacturersAndModels;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Authenticated method to get full list of facilities with their associated lists of departments
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the list's metadata if successful in obtaining it from the database, including an empty list if applicable
     */
    async getFacilitiesAndDepartments(errorCallback) {
        try {
            const token = await this.getTokenOrThrow("Only authenticated users can get lists of facilities and departments.");
            const response = await this.axiosClient.get(`facilityDepartments`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data.facilitiesAndDepartments;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Authenticated method to update a work order specified with work order information that can be modified while still open
     * @param {*} workOrderId the uniqe identifier for the work order
     * @param {*} workOrderType the type of work order (i.e. repair, preventative maintenance, etc.)
     * @param {*} workOrderAwaitStatus the optional await status to help easily identify why the work order is not yet closed (i.e. awaiting parts)
     * @param {*} problemReported the problem reported, in the case of a repair, or in the case of a PM type work order for example, a message indicating 'no issue, pm needed'
     * @param {*} problemFound the problem found after diagnosis by the technician
     * @param {*} summary details of the work performed to complete the maintenance event
     * @param {*} completionDateTime the date and time that the maintenance was completed
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the updated work order's metadata, if successfully updated
     */
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

    /**
     * Authenticated method to update a device record specified with device information that can be modified while device in active status
     * @param {*} controlNumber the overall unique identifer of a device
     * @param {*} serialNumber the manufacturer's unique identifier
     * @param {*} manufacturer the manufacturer's name
     * @param {*} model the specific model of the device
     * @param {*} facilityName the facility where this device is located
     * @param {*} assignedDepartment the department where this device is located
     * @param {*} manufactureDate the optional date of manufacture
     * @param {*} notes optional notes for the device, such as where it might typically be kept within the department, etc.
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the updated device's metadata, if successfully updated
     */
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

    /**
     * Authenticated method to retire a device specified if no work orders currently open (soft delete)
     * @param {*} controlNumber the unique identifier for the device
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the updated device's metadata, if successfully retired
     */
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

    /**
     * Authenticated method to reactivate a device specified (reversed a soft delete)
     * @param {*} controlNumber the unique identifier for the device
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the updated device's metadata, if successfully reactivated
     */
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
     * Gets the device for the given device ID (control number).
     * @param controlNumber Unique identifier for a device
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The device's metadata, if successfully retrieved
     */
    async getDevice(controlNumber, errorCallback) {
        try {
            const response = await this.axiosClient.get(`devices/${controlNumber}`);
            return response.data.device;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Gets the work order for the given work order ID.
     * @param {*} workOrderId the unique identifier for the work order
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the work order's metadata, if successfully retrieved
     */
    async getWorkOrder(workOrderId, errorCallback) {
        try {
            const response = await this.axiosClient.get(`workOrders/${workOrderId}`);
            return response.data.workOrder;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Authenticated method to create a new work order for a device.
     * @param {*} controlNumber the unique identifier of the device to which this work order is 'attached'
     * @param {*} workOrderType the type of work order (i.e. repair, preventative maintenance, etc.)
     * @param {*} problemReported the problem reported, in the case of a repair, or in the case of a PM type work order for example, a message indicating 'no issue, pm needed'
     * @param {*} problemFound the problem found after diagnosis by the technician (optional at time of creation)
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the metadata of the device's work orders, if successful in creating the new one (including the new one)
     */
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

    /**
     * Method to obtain a device's work orders
     * @param {*} controlNumber the unique identifier of the device
     * @param {*} order the order in which to sort the device's work orders
     * @param {*} errorCallback (Optional) A function to execute if the call fails.
     * @returns the metadata of the device's work orders, if successfully obtained
     */
    async getDeviceWorkOrders(controlNumber, order, errorCallback) {
        try {
            const response = await this.axiosClient.get(`devices/${controlNumber}/workOrders?order=${order}`);
            return response.data.workOrders;
        } catch (error) {
            this.handleError(error, errorCallback)
        }
    }

    /**
     * Authenticated method to add a new device to the inventory.
     * @param serialNumber The serial number of the device.
     * @param manufacturer The manufacturer of the device.
     * @param model The model of the device.
     * @param facilityName The facility where this device is located.
     * @param assignedDepartment The department within the facility to which this device is assigned.
     * @param manufactureDate The date of manufacture of this device.
     * @param notes Pertinent information on the device not otherwise stored in an attribute.
     * @param errorCallback (Optional) A function to execute if the call fails.
     * @returns The metadata of the device that has been added to the inventory.
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
     * Searches for a device.
     * @param criteria A string containing search criteria to pass to the API.
     * @param errorCallback (Optional) A function to execute if the call fails.
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
