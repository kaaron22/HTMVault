import HTMVaultClient from "../api/htmVaultClient";
import HTMVaultClient from "../api/htmVaultClient";
import Header from "../components/header";
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

class ViewWorkOrder extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'mount', 'addWorkOrderToPage']);
        this.dataStore = new DataStore();
        this.dataStore.addChangeListener(this.addWorkOrderToPage);
        this.header = new Header(this.dataStore);
        console.log("view work order constructor");
    }

    async clientLoaded() {

    }

    async addWorkOrderToPage() {
        
    }

    mount() {
        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
    }
}

const main = async () => {
    const viewWorkOrder = new ViewWorkOrder();
    viewWorkOrder.mount();
};

window.addEventListener('DOMContentLoaded', main);