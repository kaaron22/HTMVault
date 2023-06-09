import HTMVaultClient from "../api/htmVaultClient";
import Header from "../components/header";
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

class UpdateDevice extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'submit', 'mount', 'redirectToViewDevice'], this);
        this.DataStore = new DataStore();
        this.header = new Header(this.DataStore);
        console.log("update device constructor");
    }

    async clientLoaded() {
        const urlParams = new URLSearchParams(window.location.search);
        const deviceId = urlParams.get('controlNumber');
        document.getElementById('control-number').innerText = "Loading...";
        const device = await this.client.getDevice(deviceId);
        this.DataStore.set('device', device);
        document.getElementById('control-number').innerText = device.controlNumber;
    }

    async submit(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const updateButton = document.getElementById('update-device');
        const origButtonText = updateButton.innerText;
        updateButton.innerText = 'Loading...';

        const deviceControlNumber = document.getElementById('control-number').value;
        const deviceSerialNumber = document.getElementById('serial-number').value;
        const deviceManufacturer = document.getElementById('manufacturer').value;
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

        const device = await this.client.updateDevice(deviceControlNumber, deviceSerialNumber, deviceManufacturer,
            deviceModel, deviceFacilityName, deviceAssignedDepartment, manufactureDate, notes, (error) => {
            createButton.innerText = origButtonText;
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });
        this.dataStore.set('device', device);
    }

    redirectToViewDevice() {
        const device = this.dataStore.get('device');
        if (device != null) {
            window.location.href = `/device.html?controlNumber=${device.controlNumber}&order=DESCENDING`;
        }
    }

    mount() {
        document.getElementById('update-device-record').addEventListener('click', this.submit);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
    }


}

const main = async () => {
    const updateDevice = new UpdateDevice();
    updateDevice.mount();
}

window.addEventListener("DOMContentLoaded", main);