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
        // bind the class methods to this object instance to keep track of state
        this.bindClassMethods(['clientLoaded', 'submitRetire', 'submitReactivate', 'mount', 'addDeviceToPage',
         'addWorkOrdersToPage', 'cancelUpdatesDevice', 'createWorkOrder', 'displayUpdateDeviceForm', 'submitDeviceUpdates',
          'populateManufacturers', 'populateModels', 'populateFacilities', 'populateDepartments'], this);

        // the datastore to store page information
        this.dataStore = new DataStore();

        // the methods to run when an item in the datastore changes state
        this.dataStore.addChangeListener(this.addDeviceToPage);
        this.dataStore.addChangeListener(this.addWorkOrdersToPage);

        // page header, including the home page link and the login/logout button
        this.header = new Header(this.dataStore);

        console.log("view device constructor");
    }

    /**
     * Once the client is loaded, get the device and work order list metadata.
     */
    async clientLoaded() {
        // obtain the control number from the url
        const urlParams = new URLSearchParams(window.location.search);
        const deviceId = urlParams.get('controlNumber');

        // notify user that device is being loaded
        document.getElementById('control-number').innerText = "Loading Device ...";

        // call the client to get the device
        const device = await this.client.getDevice(deviceId);

        // if the device is in service, hide the button to reactivate it, as the function is inapplicable in this case
        if (device.serviceStatus == "IN_SERVICE") {
            document.getElementById('reactivate-device').classList.add('hidden');
        // otherwise, the device is retired, so hide the buttons to retire and to update the device, since those functions are inapplicable in this case
        } else {
            document.getElementById('retire-device').classList.add('hidden');
            document.getElementById('update-device').classList.add('hidden');
        }

        // update the datastore with this device, which calls the method to add the device to the page,
        // due to the change listener in the class constructor
        this.dataStore.set('device', device);

        // obtain the sort order from the url
        const order = urlParams.get('order');

        // notify user that the device's maintenance records are being loaded
        document.getElementById('work-orders').innerText = "(loading work orders...)";

        // call the client to get the work orders, returning them in the order specified (i.e. descending or ascending)
        const workOrders = await this.client.getDeviceWorkOrders(deviceId, order);

        // update the datastore with the work orders, which calls the method to add the work orders to
        // the page, due to the change listener in the class constructor
        this.dataStore.set('workOrders', workOrders);

        // pull and store a list of manufacturers and their associated models for populating the drop down selection on the update
        // device form
        const manufacturersAndModels = await this.client.getManufacturersAndModels();
        this.dataStore.set('manufacturersAndModels', manufacturersAndModels);

        // pull and store a list of facilities and their associated departments for populating the drop down selection on the
        // update device form
        const facilitiesAndDepartments = await this.client.getFacilitiesAndDepartments();
        this.dataStore.set('facilitiesAndDepartments', facilitiesAndDepartments);
    }

    /**
     * Add the header to the page, load the HTMVaultClient, and initialize the page information
     */
    mount() {
        // event listeners for button clicks and drop down selections
        document.getElementById('retire-device').addEventListener('click', this.submitRetire);
        document.getElementById('reactivate-device').addEventListener('click', this.submitReactivate);
        document.getElementById('add-new-work-order').addEventListener('click', this.createWorkOrder);
        document.getElementById('update-device').addEventListener('click', this.displayUpdateDeviceForm);
        document.getElementById('submit-updates-device').addEventListener('click', this.submitDeviceUpdates);
        document.getElementById('cancel-updates-device').addEventListener('click', this.cancelUpdatesDevice);
        document.getElementById('manufacturer-drop-down').addEventListener('change', this.populateModels);
        document.getElementById('facility-drop-down').addEventListener('change', this.populateDepartments);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
    }

    /**
     * Method for submitting updates when the corresponding button is clicked
     */
    async submitDeviceUpdates(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('update-error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // a success message to unhide if the endpoint succeeds
        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Device successfully updated.';
        successMessageDisplay.classList.add('hidden');

        // inform the user that the update submission is being processed
        const updateButton = document.getElementById('update-device');
        const origButtonText = updateButton.innerText;
        updateButton.innerText = 'Updating...';

        // obtain the input values being submitted for update in order to pass to the client
        const deviceControlNumber = document.getElementById('control-number').innerText;
        const deviceSerialNumber = document.getElementById('update-serial-number').value;
        const deviceManufacturer = document.getElementById('manufacturer-drop-down').value;
        const deviceModel = document.getElementById('model-drop-down').value;
        const deviceFacilityName = document.getElementById('facility-drop-down').value;
        const deviceAssignedDepartment = document.getElementById('department-drop-down').value;
        const deviceManufactureDate = document.getElementById('update-manufacture-date').value;
        const deviceNotes = document.getElementById('update-notes').value;

        // if the optional manufacture date is empty, set the value to null
        let manufactureDate;
        if (deviceManufactureDate.length < 1) {
            manufactureDate = null;
        } else {
            manufactureDate = deviceManufactureDate;
        }

        // if the optional device notes is empty, set the value to null
        let notes;
        if (deviceNotes.length < 1) {
            notes = null;
        } else {
            notes = deviceNotes;
        }

        // the client call to update the device with the input provided
        const device = await this.client.updateDevice(deviceControlNumber, deviceSerialNumber, deviceManufacturer, 
            deviceModel, deviceFacilityName, deviceAssignedDepartment, manufactureDate, notes, (error) => {
                // reset the button to indicate the process is complete (when an error occurs)
                updateButton.innerText = origButtonText;

                // unhide the error message element
                errorMessageDisplay.innerText = `Error: ${error.message}`;
                errorMessageDisplay.classList.remove('hidden');
        });

        // if the update succeeds, the update device form will be hidden and the device information will be
        // unhidden, but the button text needs to be reset for the next time an attempt will potentially be made
        // to update the device
        updateButton.innerText = origButtonText;

        // if unsuccessful obtaining the device from the backend, return without updating the device in the datastore
        if (null == device) {
            return;
        }

        // otherwise, update the device in the datastore
        this.dataStore.set('device', device);

        // temporarily display the success message for defined period of time
        successMessageDisplay.classList.remove('hidden');
        setTimeout(() => {
            successMessageDisplay.classList.add('hidden');
        }, 3500);

        // hide the update device form; unhide the device record, work orders list, and the create new work order form
        const deviceRecordDiv = document.getElementById('device-record-div');
        const updateDeviceDiv = document.getElementById('update-device-div');
        const workOrdersDiv = document.getElementById('work-orders-div');
        const createWorkOrderDiv = document.getElementById('create-work-order');
        updateDeviceDiv.classList.add('hidden');
        deviceRecordDiv.classList.remove('hidden');
        workOrdersDiv.classList.remove('hidden');
        createWorkOrderDiv.classList.remove('hidden');
    }

    /**
     * Method for displaying the update device form, populated with the device's current values for the information
     * that can potentially be edited
     */
    async displayUpdateDeviceForm(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // reset the create work order form once the button is clicked that begins the update device
        // process (by displaying the update device form)
        document.getElementById("create-new-work-order-form").reset();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const updateDeviceErrorMessageDisplay = document.getElementById('update-error-message');
        updateDeviceErrorMessageDisplay.innerText = ``;
        updateDeviceErrorMessageDisplay.classList.add('hidden');

        // get the device from the datastore in order to populate current values on the update device form
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

        // populate the manufacturer drop down for the update device form, then initialize the form's selection with the current manufacturer
        this.populateManufacturers();

        document.getElementById('update-control-number').innerText = device.controlNumber;
        document.getElementById('update-serial-number').value = device.serialNumber;
        document.getElementById('manufacturer-drop-down').value = device.manufacturer;

        // with the form's manufacturer pre-selected, populate the models and do the same (pre-select), based on the current device's model
        this.populateModels();

        document.getElementById('model-drop-down').value = device.model;
        document.getElementById('update-manufacture-date').value = manufactureDate;

        // populate the facility drop down for the update device form, then initialize the form's selection with the current facility
        this.populateFacilities();

        document.getElementById('facility-drop-down').value = device.facilityName;

        // with the form's facility then pre-selected, populate the departments and do the same (pre-select), based on the current device's model
        this.populateDepartments();

        document.getElementById('department-drop-down').value = device.assignedDepartment;
        document.getElementById('update-notes').value = notes;

        // hide the divs that display the full device details, the list of work orders and the create work order form, while unhiding the update device form
        const deviceRecordDiv = document.getElementById('device-record-div');
        const updateDeviceDiv = document.getElementById('update-device-div');
        const workOrdersDiv = document.getElementById('work-orders-div');
        const createWorkOrderDiv = document.getElementById('create-work-order');
        deviceRecordDiv.classList.add('hidden');
        updateDeviceDiv.classList.remove('hidden');
        workOrdersDiv.classList.add('hidden');
        createWorkOrderDiv.classList.add('hidden');

    }

    /**
     * Populate the add device form's drop down list of manufacturers with those available
     */
    async populateManufacturers() {
        // get the list of manufacturers and their models from the datastore
        const manufacturersAndModels = this.dataStore.get('manufacturersAndModels');
        
        // the opening html for the manufacturer drop down list element
        let manufacturersHtml = '';
        manufacturersHtml += `<label for="manufacturer-drop-down">Manufacturer</label>
                                <select class=validated-field id="manufacturer-drop-down" required>
                                <option value="">Select a Manufacturer</option>
                                `

        let manufacturer;
        // iteratively populate each available option in the drop down list
        for (manufacturer of manufacturersAndModels) {
            manufacturersHtml += `<option value="${manufacturer.manufacturer}">${manufacturer.manufacturer}</option>
                                    `
        }

        // the closing html for the drop down list element
        manufacturersHtml += `</select>`

        // setting the element with our generated HTML
        document.getElementById('manufacturer-drop-down').innerHTML = manufacturersHtml;
    }

    /**
     * Populate the add device form's drop down list of models with those available for the manufacturer previously selected on the form
     */
    async populateModels() {
        // the selected manufacturer
        const selectedManufacturer = document.getElementById('manufacturer-drop-down').value;
        
        // get the list of manufacturers and their models from the datastore
        const manufacturersAndModels = this.dataStore.get('manufacturersAndModels');

        // the opening html for the models drop down list element
        let modelsHtml = '';
        modelsHtml += `<label for="model-drop-down">Model</label>
                           <select class=validated-field id="model-drop-down" required>
                           <option value="">Select a Model</option>
                           `

        let manufacturer;
        for (manufacturer of manufacturersAndModels) {
            // find the selected manufacturer in the datastore in order to access it's associated list of models
            if (manufacturer.manufacturer == selectedManufacturer) {
                let model;
                // iteratively populate each available option in the drop down list with the list of models for this manufacturer
                for (model of manufacturer.models) {
                    modelsHtml += `<option value="${model}">${model}</option>
                                    `
                }
            }
        }

        // the closing html for the drop down list element
        modelsHtml += `</select>`

        // setting the element with our generated HTML
        document.getElementById('model-drop-down').innerHTML = modelsHtml;
    }

    /**
     * Populate the add device form's drop down list of facilities with those available
     */
    async populateFacilities() {
        // get the list of facilities and their departments from the datastore
        const facilitiesAndDepartments = this.dataStore.get('facilitiesAndDepartments');

        let facilitiesHtml = '';
        // the opening html for the facility drop down list element
        facilitiesHtml += `<label for="facility-drop-down">Facility</label>
                                <select class=validated-field id="facility-drop-down" required>
                                <option value="">Select a Facility</option>
                                `

        let facility;
        // iteratively populate each available option in the drop down list
        for (facility of facilitiesAndDepartments) {
            facilitiesHtml += `<option value="${facility.facility}">${facility.facility}</option>
                                    `
        }

        // the closing html for the drop down list element
        facilitiesHtml += `</select>`

        // setting the element with our generated HTML
        document.getElementById('facility-drop-down').innerHTML = facilitiesHtml;
    }

    /**
     * Populate the add device form's drop down list of departmenst with those available for the facility previously selected on the form
     */
    async populateDepartments() {
        // the selected facility
        const selectedFacility = document.getElementById('facility-drop-down').value;

        // get the list of facilites and their departments from the datastore
        const facilitiesAndDepartments = this.dataStore.get('facilitiesAndDepartments');

        // the opening html for the departments drop down list element
        let departmentsHtml = '';
        departmentsHtml += `<label for="department-drop-down">Department</label>
                           <select class=validated-field id="department-drop-down" required>
                           <option value="">Select a Department</option>
                           `

        let facility;
        // find the selected facility in the datastore in order to access it's associated list of departments
        for (facility of facilitiesAndDepartments) {
            if (facility.facility == selectedFacility) {
                let department;
                // iteratively populate each available option in the drop down list with the list of departments for this facility
                for (department of facility.departments) {
                    departmentsHtml += `<option value="${department}">${department}</option>
                                    `
                }
            }
        }

        // the closing html for the drop down list element
        departmentsHtml += `</select>`

        // setting the element with our generated HTML
        document.getElementById('department-drop-down').innerHTML = departmentsHtml;
    }

    /**
     * Method to run when the retire device button is pressed. Calls the HTMVaultService to retire the
     * device.
     */
    async submitRetire(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // inform the user that the retire device submission is being processed
        const retireButton = document.getElementById('retire-device');
        const origButtonText = retireButton.innerText;
        retireButton.innerText = 'Retiring...';

        // obtain the device id (control number) of the device to pass to the client
        const device = this.dataStore.get('device');
        const deviceControlNumber = device.controlNumber;

        // if the control number is empty, set the value to null (the backend checks for a blank / empty value and throws a corresponding error/exception)
        let controlNumber;
        if (deviceControlNumber.length < 1) {
            controlNumber = "";
        } else {
            controlNumber = deviceControlNumber;
        }

        // the client call to retire the device, if conditions are met (i.e. the backend checks for open work orders, which prevents device retirement)
        const retiredDevice = await this.client.retireDevice(controlNumber, (error) => {
            errorMessageDisplay.innerText = `Error: ${error.message}`
            errorMessageDisplay.classList.remove('hidden');
        });

        // update the datastore following a successful request and update the applicable buttons displayed for the device
        if (retiredDevice != null) {
            this.dataStore.set('device', retiredDevice);
            document.getElementById('retire-device').classList.add('hidden');
            document.getElementById('update-device').classList.add('hidden');
            document.getElementById('reactivate-device').classList.remove('hidden');
            document.getElementById('create-work-order').classList.add('hidden');
        }

        // reset the retire device button's text
        retireButton.innerText = origButtonText;
    }

    /**
     * Method to run when the reactivate device button is pressed. Calls the HTMVaultService to reactivate the
     * device (returns it to an active status).
     */
    async submitReactivate(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // inform the user that the update submission is being processed
        const reactivateButton = document.getElementById('reactivate-device');
        const origButtonText = reactivateButton.innerText;
        reactivateButton.innerText = 'Reactivating...';

        // obtain the device id (control number) of the device to pass to the client
        const device = this.dataStore.get('device');
        const deviceControlNumber = device.controlNumber;

        // if the control number is empty, set the value to null (the backend checks for a blank / empty value and throws a corresponding error/exception)
        let controlNumber;
        if (deviceControlNumber.length < 1) {
            controlNumber = "";
        } else {
            controlNumber = deviceControlNumber;
        }

        // the client call to reactivate the device
        const reactivatedDevice = await this.client.reactivateDevice(controlNumber, (error) => {
            errorMessageDisplay.innerText = `Error: ${error.message}`
            errorMessageDisplay.classList.remove('hidden');
        });

        // if successfull, update the datastore and update the applicable buttons displayed for the device
        if (reactivatedDevice != null) {
            this.dataStore.set('device', reactivatedDevice);
            document.getElementById('retire-device').classList.remove('hidden');
            document.getElementById('update-device').classList.remove('hidden');
            document.getElementById('reactivate-device').classList.add('hidden');
            document.getElementById('create-work-order').classList.remove('hidden');
        }

        // reset the retire device button's text
        reactivateButton.innerText = origButtonText;
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

        // if no work orders, display a message indicating, so the user knows the work order list is not still being retrieved
        if (workOrders == null || workOrders.length == 0) {
            document.getElementById('work-orders').innerHTML = 'No work orders found';
            return;
        }

        let workOrderSummaryHtml = '';
        // table header row for work order list
        workOrderSummaryHtml += `<table id="work-orders">
                                   <tr>
                                       <th>Work Order ID</th>
                                       <th>Type</th>
                                       <th>Completion Status</th>
                                       <th>Created</th>
                                       <th>Completed</th>
                                   </tr>`

        let workOrderSummary;
        // iteratively populate the html needed for the table of work orders
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

        // closing html for table element
        workOrderSummaryHtml += `</table>`

        // update the element with the information
        document.getElementById('work-orders').innerHTML = workOrderSummaryHtml;
    }

    /**
     * Cancels an update device form by returning the the device details view, including the list of work orders, and the create work order form,
     * while hiding the update device form
     */
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
     * Method to run when the create work order submit button is pressed. Call the HTM Vault service to add the work order.
     */
    async createWorkOrder() {

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message-device-record-change');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // a success message to unhide if the endpoint succeeds
        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Work order successfully created';
        successMessageDisplay.classList.add('hidden');

        // get the device from the datastore, if present
        const device = this.dataStore.get('device');
        if (device == null) {
            return;
        }

        // inform user that create new work order request is being processed
        document.getElementById('add-new-work-order').innerText = 'Adding...';
        const controlNumber = document.getElementById('control-number').innerText;
        const workOrderType = document.getElementById('workOrderType').value;
        const problemReported = document.getElementById('problem-reported').value;
        const problemFound = document.getElementById('problem-found').value;
        const urlParams = new URLSearchParams(window.location.search);
        const order = urlParams.get('order');

        // the client call to obtain this device's list of work orders
        const workOrderList = await this.client.createWorkOrder(controlNumber, workOrderType, problemReported, problemFound, order, (error) => {
            // if there's an error, set the error element with the error returned and unhide the element
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');           
        });

        // if successful in creating the work order, update the datastore with the updated list of work orders, which will cause the view to update accordingly
        // due to the change listener
        if (!(workOrderList == null)) {
            this.dataStore.set('workOrders', workOrderList);
            // reset the create work order form for the next potential request
            document.getElementById("create-new-work-order-form").reset();

            // temporarily display a success message, for the amount of time specified
            successMessageDisplay.classList.remove('hidden');
            setTimeout(() => {
                successMessageDisplay.classList.add('hidden');
            }, 3500);
        }

        // reset the create work order button text
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
