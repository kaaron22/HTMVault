import HTMVaultClient from '../api/htmVaultClient';
import Header from '../components/header';
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

/**
 * Logic needed for the view device page of the website.
 */
class ViewDevice extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'submitRetire', 'submitReactivate', 'mount', 'addDeviceToPage', 'addWorkOrdersToPage', 'cancelUpdatesDevice', 'createWorkOrder', 'displayUpdateDeviceForm', 'submitDeviceUpdates', 'populateManufacturers', 'populateModels'], this);
        this.dataStore = new DataStore();
        this.dataStore.addChangeListener(this.addDeviceToPage);
        this.dataStore.addChangeListener(this.addWorkOrdersToPage);
        //this.dataStore.addChangeListener(this.populateManufacturers);
        this.header = new Header(this.dataStore);
        console.log("view device constructor");
    }

    /**
     * Once the client is loaded, get the device metadata and work order list.
     */
    async clientLoaded() {
        const urlParams = new URLSearchParams(window.location.search);
        const deviceId = urlParams.get('controlNumber');
        document.getElementById('control-number').innerText = "Loading Device ...";
        const device = await this.client.getDevice(deviceId);
        if (device.serviceStatus == "IN_SERVICE") {
            document.getElementById('reactivate-device').classList.add('hidden');
        } else {
            document.getElementById('retire-device').classList.add('hidden');
            document.getElementById('update-device').classList.add('hidden');
        }
        this.dataStore.set('device', device);
        const order = urlParams.get('order');
        document.getElementById('work-orders').innerText = "(loading work orders...)";
        const workOrders = await this.client.getDeviceWorkOrders(deviceId, order);
        this.dataStore.set('workOrders', workOrders);

        const manufacturersAndModels = await this.client.getManufacturersAndModels();
        this.dataStore.set('manufacturersAndModels', manufacturersAndModels);

    }

    async submitDeviceUpdates(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('update-error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Device successfully updated.';
        successMessageDisplay.classList.add('hidden');

        const updateButton = document.getElementById('update-device');
        const origButtonText = updateButton.innerText;
        updateButton.innerText = 'Updating...';

        const deviceControlNumber = document.getElementById('control-number').innerText;
        const deviceSerialNumber = document.getElementById('update-serial-number').value;
        const deviceManufacturer = document.getElementById('update-manufacturer').value;
        const deviceModel = document.getElementById('update-model').value;
        const deviceFacilityName = document.getElementById('update-facility-name').value;
        const deviceAssignedDepartment = document.getElementById('update-assigned-department').value;
        const deviceManufactureDate = document.getElementById('update-manufacture-date').value;
        const deviceNotes = document.getElementById('update-notes').value;

        let manufactureDate;
        if (deviceManufactureDate.length < 1) {
            manufactureDate = null;
        } else {
            manufactureDate = deviceManufactureDate;
        }

        let notes;
        if (deviceNotes.length < 1) {
            notes = null;
        } else {
            notes = deviceNotes;
        }

        const device = await this.client.updateDevice(deviceControlNumber, deviceSerialNumber, deviceManufacturer,
            deviceModel, deviceFacilityName, deviceAssignedDepartment, manufactureDate, notes, (error) => {
            updateButton.innerText = origButtonText;
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });

        updateButton.innerText = origButtonText;

        if (null == device) {
            return;
        }

        this.dataStore.set('device', device);

        successMessageDisplay.classList.remove('hidden');
        setTimeout(() => {
            successMessageDisplay.classList.add('hidden');
        }, 3500);

        const deviceRecordDiv = document.getElementById('device-record-div');
        const updateDeviceDiv = document.getElementById('update-device-div');
        const workOrdersDiv = document.getElementById('work-orders-div');
        const createWorkOrderDiv = document.getElementById('create-work-order');
        updateDeviceDiv.classList.add('hidden');
        deviceRecordDiv.classList.remove('hidden');
        workOrdersDiv.classList.remove('hidden');
        createWorkOrderDiv.classList.remove('hidden');

    }

    async displayUpdateDeviceForm(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        document.getElementById("create-new-work-order-form").reset();

        const updateDeviceErrorMessageDisplay = document.getElementById('update-error-message');
        updateDeviceErrorMessageDisplay.innerText = ``;
        updateDeviceErrorMessageDisplay.classList.add('hidden');

        const device = this.dataStore.get('device');
        const deviceManufactureDate = device.manufactureDate;
        const deviceNotes = device.notes;

        let manufactureDate;
        if (null == deviceManufactureDate || deviceManufactureDate.length < 1) {
            manufactureDate = "";
        } else {
            manufactureDate = deviceManufactureDate;
        }

        let notes;
        if (null == deviceNotes || deviceNotes.length < 1) {
            notes = "";
        } else {
            notes = deviceNotes;
        }

        this.populateManufacturers();

        document.getElementById('update-control-number').innerText = device.controlNumber;
        document.getElementById('update-serial-number').value = device.serialNumber;
        document.getElementById('update-manufacturer').value = device.manufacturer;
        document.getElementById('update-model').value = device.model;
        document.getElementById('update-manufacture-date').value = manufactureDate
        document.getElementById('update-facility-name').value = device.facilityName;
        document.getElementById('update-assigned-department').value = device.assignedDepartment;
        document.getElementById('update-notes').value = notes;

        const deviceRecordDiv = document.getElementById('device-record-div');
        const updateDeviceDiv = document.getElementById('update-device-div');
        const workOrdersDiv = document.getElementById('work-orders-div');
        const createWorkOrderDiv = document.getElementById('create-work-order');
        deviceRecordDiv.classList.add('hidden');
        updateDeviceDiv.classList.remove('hidden');
        workOrdersDiv.classList.add('hidden');
        createWorkOrderDiv.classList.add('hidden');

    }

    populateManufacturers() {
        const manufacturersAndModels = this.dataStore.get('manufacturersAndModels');
        let manufacturersHtml = '';
        manufacturersHtml += `<label for="manufacturer-drop-down">Manufacturer</label>
                                <select class=validated-field id="manufacturer-drop-down" required>
                                <option value="">Select a Manufacturer</option>
                                `

        let manufacturer;
        for (manufacturer of manufacturersAndModels) {
            manufacturersHtml += `<option value="${manufacturer.manufacturer}">${manufacturer.manufacturer}</option>
                                    `
        }
        manufacturersHtml += `</select>`
        document.getElementById('manufacturer-drop-down').innerHTML = manufacturersHtml;
    }

    populateModels() {
        const selectedManufacturer = document.getElementById('manufacturer-drop-down').value;
        const manufacturersAndModels = this.dataStore.get('manufacturersAndModels');

        let modelsHtml = '';
        modelsHtml += `<label for="model-drop-down">Model</label>
                           <select class=validated-field id="model-drop-down" required>
                           <option value="">Select a Model</option>
                           `

        let manufacturer;
        for (manufacturer of manufacturersAndModels) {
            if (manufacturer.manufacturer == selectedManufacturer) {
                let model;
                for (model of manufacturer.models) {
                    modelsHtml += `<option value="${model}">${model}</option>
                                    `
                }
            }
        }
        modelsHtml += `</select>`
        document.getElementById('model-drop-down').innerHTML = modelsHtml;
    }

    async submitRetire(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const retireButton = document.getElementById('retire-device');
        const origButtonText = retireButton.innerText;
        retireButton.innerText = 'Retiring...';

        const device = this.dataStore.get('device');
        const deviceControlNumber = device.controlNumber;

        let controlNumber;
        if (deviceControlNumber.length < 1) {
            controlNumber = "";
        } else {
            controlNumber = deviceControlNumber;
        }

        const retiredDevice = await this.client.retireDevice(controlNumber, (error) => {
            errorMessageDisplay.innerText = `Error: ${error.message}`
            errorMessageDisplay.classList.remove('hidden');
        });

        if (retiredDevice != null) {
            this.dataStore.set('device', retiredDevice);
            document.getElementById('retire-device').classList.add('hidden');
            document.getElementById('update-device').classList.add('hidden');
            document.getElementById('reactivate-device').classList.remove('hidden');
            document.getElementById('create-work-order').classList.add('hidden');
        }
        retireButton.innerText = origButtonText;
    }

    async submitReactivate(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const reactivateButton = document.getElementById('reactivate-device');
        const origButtonText = reactivateButton.innerText;
        reactivateButton.innerText = 'Reactivating...';

        const device = this.dataStore.get('device');
        const deviceControlNumber = device.controlNumber;

        let controlNumber;
        if (deviceControlNumber.length < 1) {
            controlNumber = "";
        } else {
            controlNumber = deviceControlNumber;
        }

        const reactivatedDevice = await this.client.reactivateDevice(controlNumber, (error) => {
            errorMessageDisplay.innerText = `Error: ${error.message}`
            errorMessageDisplay.classList.remove('hidden');
        });

        if (reactivatedDevice != null) {
            this.dataStore.set('device', reactivatedDevice);
            document.getElementById('retire-device').classList.remove('hidden');
            document.getElementById('update-device').classList.remove('hidden');
            document.getElementById('reactivate-device').classList.add('hidden');
            document.getElementById('create-work-order').classList.remove('hidden');
        }
        reactivateButton.innerText = origButtonText;
    }

    /**
     * Add the header to the page and load the HTMVaultClient.
     */
    mount() {
        document.getElementById('retire-device').addEventListener('click', this.submitRetire);
        document.getElementById('reactivate-device').addEventListener('click', this.submitReactivate);
        document.getElementById('add-new-work-order').addEventListener('click', this.createWorkOrder);
        document.getElementById('update-device').addEventListener('click', this.displayUpdateDeviceForm);
        document.getElementById('submit-updates-device').addEventListener('click', this.submitDeviceUpdates);
        document.getElementById('cancel-updates-device').addEventListener('click', this.cancelUpdatesDevice);
        document.getElementById('manufacturer-drop-down').addEventListener('change', this.populateModels);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
    }

    /**
     * When the device is updated in the datastore, update the device metadata on the page.
     */
    addDeviceToPage() {
        const device = this.dataStore.get('device');
        if (device == null) {
            return;
        }
        document.getElementById('control-number').innerText = device.controlNumber;
        document.getElementById('serial-number').innerText = device.serialNumber;
        document.getElementById('manufacturer').innerText = device.manufacturer;
        document.getElementById('model').innerText = device.model;
        document.getElementById('manufacture-date').innerText = device.manufactureDate;
        document.getElementById('service-status').innerText = device.serviceStatus;
        document.getElementById('facility-name').innerText = device.facilityName;
        document.getElementById('assigned-department').innerText = device.assignedDepartment;
        document.getElementById('compliance-through-date').innerText = device.complianceThroughDate;
        document.getElementById('last-pm-completion-date').innerText = device.lastPmCompletionDate;
        document.getElementById('next-pm-due-date').innerText = device.nextPmDueDate;
        document.getElementById('pm-frequency-months').innerText = device.maintenanceFrequencyInMonths;
        document.getElementById('inventory-add-date').innerText = device.inventoryAddDate;
        document.getElementById('added-by-id').innerText = device.addedById;
        document.getElementById('added-by-name').innerText = device.addedByName;
        document.getElementById('device-notes').innerText = device.notes;
    }

    /**
     * When the work orders are updated in the datastore, update the list of work orders on the page.
     */
    addWorkOrdersToPage() {
        const workOrders = this.dataStore.get('workOrders')

        if (workOrders == null || workOrders.length == 0) {
            document.getElementById('work-orders').innerHTML = 'No work orders found';
            return;
        }

        let workOrderSummaryHtml = '';
        // table header row
        workOrderSummaryHtml += `<table id="work-orders">
                                   <tr>
                                       <th>Work Order ID</th>
                                       <th>Type</th>
                                       <th>Completion Status</th>
                                       <th>Created</th>
                                       <th>Completed</th>
                                   </tr>`

        let workOrderSummary;
        for (workOrderSummary of workOrders) {
            workOrderSummaryHtml += `
                <tr>
                <td><a href="workOrder.html?workOrderId=${workOrderSummary.workOrderId}">${workOrderSummary.workOrderId}</a></td>
                    <td>${workOrderSummary.workOrderType}</td>
                    <td>${workOrderSummary.workOrderCompletionStatus}</td>
                    <td>${workOrderSummary.creationDateTime}</td>
                    <td>${workOrderSummary.completionDateTime}</td>
                </tr>`
        }
        workOrderSummaryHtml += `</table>`
        document.getElementById('work-orders').innerHTML = workOrderSummaryHtml;
    }

    async cancelUpdatesDevice() {
        const deviceRecordDiv = document.getElementById('device-record-div');
        const updateDeviceDiv = document.getElementById('update-device-div');
        const workOrdersDiv = document.getElementById('work-orders-div');
        const createWorkOrderDiv = document.getElementById('create-work-order');
        deviceRecordDiv.classList.remove('hidden');
        updateDeviceDiv.classList.add('hidden');
        workOrdersDiv.classList.remove('hidden');
        createWorkOrderDiv.classList.remove('hidden');
    }

    /**
     * Method to run when the add song playlist submit button is pressed. Call the MusicPlaylistService to add a song to the
     * playlist.
     */
    async createWorkOrder() {

        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Work order successfully created';
        successMessageDisplay.classList.add('hidden');

        const device = this.dataStore.get('device');
        if (device == null) {
            return;
        }

        document.getElementById('add-new-work-order').innerText = 'Adding...';
        const controlNumber = document.getElementById('control-number').innerText;
        const workOrderType = document.getElementById('workOrderType').value;
        const problemReported = document.getElementById('problem-reported').value;
        const problemFound = document.getElementById('problem-found').value;
        const urlParams = new URLSearchParams(window.location.search);
        const order = urlParams.get('order');

        const workOrderList = await this.client.createWorkOrder(controlNumber, workOrderType, problemReported, problemFound, order, (error) => {
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');           
        });

        if (!(workOrderList == null)) {
            this.dataStore.set('workOrders', workOrderList);
            document.getElementById("create-new-work-order-form").reset();
            successMessageDisplay.classList.remove('hidden');

            setTimeout(() => {
                successMessageDisplay.classList.add('hidden');
            }, 3500);
        }

        document.getElementById('add-new-work-order').innerText = 'Create New Work Order';
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const viewDevice = new ViewDevice();
    viewDevice.mount();
};

window.addEventListener('DOMContentLoaded', main);
