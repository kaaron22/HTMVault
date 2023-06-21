import HTMVaultClient from '../api/htmVaultClient';
import Header from '../components/header';
import BindingClass from '../util/bindingClass';
import DataStore from '../util/DataStore';

/**
 * Logic needed for the add device page of the website.
 */
class AddDevice extends BindingClass {
    constructor() {
        super();
        // bind the class methods to this object instance to keep track of state
        this.bindClassMethods(['mount', 'clientLoaded', 'submit', 'redirectToViewDevice', 'populateManufacturers', 'populateModels', 'populateFacilities', 'populateDepartments'], this);
        
        // the datastore to store page information
        this.dataStore = new DataStore();

        // page header, including the home page link and the login/logout button
        this.header = new Header(this.dataStore);
    }

    /**
     * Once the client is loaded, get the manufacturers/models and facilities/departments metadata to populate cascading drop down lists for the add device form.
     */
    async clientLoaded() {

        // client call to obtain the list of manufacturers with their associated models
        const manufacturersAndModels = await this.client.getManufacturersAndModels();
        if (!(null == manufacturersAndModels)) {
            // update the datastore with results, if successfully obtained
            this.dataStore.set('manufacturersAndModels', manufacturersAndModels);

            // we first populate the manufacturers (models not populated until a manufacturer selection is made)
            this.populateManufacturers();
        }

        // client call to obtain the list of facilities with their associated departments
        const facilitiesAndDepartments = await this.client.getFacilitiesAndDepartments();
        if (!(null == facilitiesAndDepartments)) {
            // update the datastore with results, if successfully obtained
            this.dataStore.set('facilitiesAndDepartments', facilitiesAndDepartments);

            // we first populate the facilities (departments not populated until a facility selection is made)
            this.populateFacilities();
        }
    }

    /**
     * Add the header to the page and load the HTMVaultClient.
     */
    mount() {
        // listeners for selecting a manufacturer or a facility, as well as submitting the add device form
        // upon a selection for manufacturer or facility, then calls the subsequent method to populate the
        // models or departments drop down based on the selection
        document.getElementById('create').addEventListener('click', this.submit);
        document.getElementById('manufacturer-drop-down').addEventListener('change', this.populateModels);
        document.getElementById('facility-drop-down').addEventListener('change', this.populateDepartments);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();

        // initialize the page/add device form
        this.clientLoaded();
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

        let departmentsHtml = '';
        // the opening html for the departments drop down list element
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
     * Method to run when the add device submit button is pressed. Calls the HTMVaultService to add the
     * device.
     */
    async submit(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // inform the user that the update submission is being processed
        const createButton = document.getElementById('create');
        const origButtonText = createButton.innerText;
        createButton.innerText = 'Loading...';

        // obtain the input values being submitted for update in order to pass to the client
        const deviceSerialNumber = document.getElementById('serial-number').value;
        const deviceManufacturer = document.getElementById('manufacturer-drop-down').value;
        const deviceModel = document.getElementById('model-drop-down').value;
        const deviceFacilityName = document.getElementById('facility-drop-down').value;
        const deviceAssignedDepartment = document.getElementById('department-drop-down').value;
        const deviceManufactureDate = document.getElementById('manufacture-date').value;
        const deviceNotes = document.getElementById('notes').value;

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

        // the client call to add the device with the input provided
        const device = await this.client.addDevice(deviceSerialNumber, deviceManufacturer, deviceModel, deviceFacilityName, deviceAssignedDepartment, manufactureDate, notes, (error) => {
            // reset the button to indicate the process is complete (when an error occurs)
            createButton.innerText = origButtonText;
            
            // unhide the error message element
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });

        // device successfully added (error did not occur), update the datastore, causing a redirect to view the full details of the device
        if (!(null == device)) {
            this.dataStore.set('device', device);
            this.redirectToViewDevice();
        }
    }

    /**
     * When the device is updated in the datastore, redirect to the view device page, which displays full device details, along with a sorted list of work orders (none initially).
     */
    redirectToViewDevice() {
        const device = this.dataStore.get('device');
        if (device != null) {
            window.location.href = `/device.html?controlNumber=${device.controlNumber}&order=DESCENDING`;
        }
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const addDevice = new AddDevice();
    addDevice.mount();
};

window.addEventListener('DOMContentLoaded', main);
