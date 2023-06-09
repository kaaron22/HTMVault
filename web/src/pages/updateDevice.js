import HTMVaultClient from "../api/htmVaultClient";
import Header from "../components/header";
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";



const main = async () => {
    const updateDevice = new UpdateDevice();
    updateDevice.mount();
}

window.addEventListener("DOMContentLoaded", main);