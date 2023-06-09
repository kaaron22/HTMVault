import HTMVaultClient from "../api/htmVaultClient";
import Header from "../components/header";
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

class UpdateDevice extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'submit', 'mount', 'redirectToViewDevice'], this);
        this.DataStore = new DataStore();
        this.DataStore.addChangeListener(this.redirectToViewDevice);
        this.header = new Header(this.DataStore);
        console.log("update device constructor");
    }

    async clientLoaded() {
        const urlParams = new URLSearchParams(window.location.search);
        const deviceId = urlParams.get('controlNumber');
        document.getElementById('control-number').innerText = "Loading...";
        const device = await this.clientLoaded.getDevice(deviceId);
        this.DataStore.set('device', device);
    }

    mount() {
        document.getElementById('update-device-record').addEventListener('click', this.submit);
    }

}

const main = async () => {
    const updateDevice = new UpdateDevice();
    updateDevice.mount();
}

window.addEventListener("DOMContentLoaded", main);