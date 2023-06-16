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
        this.bindClassMethods(['mount', 'clientLoaded', 'submit', 'redirectToViewDevice', 'populateManufacturers'], this);
        this.dataStore = new DataStore();
        this.dataStore.addChangeListener(this.redirectToViewDevice);
        this.dataStore.addChangeListener(this.populateManufacturers);
        this.header = new Header(this.dataStore);
    }

    async clientLoaded() {
        const manufacturersAndModels = await this.client.getManufacturersAndModels();
        this.dataStore.set('manufacturersAndModels', manufacturersAndModels);
    }

    /**
     * Add the header to the page and load the HTMVaultClient.
     */
    mount() {
        document.getElementById('create').addEventListener('click', this.submit);
        document.getElementById('manufacturer-drop-down').addEventListener('change', this.populateModels);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
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
            console.log(manufacturer);
            console.log(manufacturer.manufacturer);
            manufacturersHtml += `<option value="${manufacturer.manufacturer}">${manufacturer.manufacturer}</option>
                                    `
        }
        manufacturersHtml += `</select>`
        console.log(manufacturersHtml);
        document.getElementById('manufacturer-drop-down').innerHTML = manufacturersHtml;
    }

    /**
     * Method to run when the add device submit button is pressed. Call the HTMVaultService to add the
     * device.
     */
    async submit(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const createButton = document.getElementById('create');
        const origButtonText = createButton.innerText;
        createButton.innerText = 'Loading...';

        const deviceSerialNumber = document.getElementById('serial-number').value;
        const deviceManufacturer = document.getElementById('manufacturer-drop-down').value;
        const deviceModel = document.getElementById('model').value;
        const deviceFacilityName = document.getElementById('facility-name').value;
        const deviceAssignedDepartment = document.getElementById('assigned-department').value;
        const deviceManufactureDate = document.getElementById('manufacture-date').value;
        const deviceNotes = document.getElementById('notes').value;

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

        const device = await this.client.addDevice(deviceSerialNumber, deviceManufacturer,
         deviceModel, deviceFacilityName, deviceAssignedDepartment, manufactureDate, notes, (error) => {
            createButton.innerText = origButtonText;
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });
        this.dataStore.set('device', device);
    }

    /**
     * When the device is updated in the datastore, redirect to the view device page.
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
