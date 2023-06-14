import HTMVaultClient from "../api/htmVaultClient";
import Header from "../components/header";
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

class ViewWorkOrder extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'mount', 'addWorkOrderToPage'], this);
        this.dataStore = new DataStore();
        this.dataStore.addChangeListener(this.addWorkOrderToPage);
        this.header = new Header(this.dataStore);
        console.log("view work order constructor");
    }

    async displayUpdateWorkOrderForm(evt) {
        evt.preventOrDefault();

        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const workOrder = this.dataStore.get('workOrder');
        const recordWorkOrderAwaitStatus = workOrder.workOrderAwaitStatus;
        const recordProblemFound = workOrder.problemFound;
        const recordSummary = workOrder.summary;
        const recordCompletionDateTime = workOrder.completionDateTime;

        let workOrderAwaitStatus;
        if (null == recordWorkOrderAwaitStatus || recordWorkOrderAwaitStatus.length < 1) {
            workOrderAwaitStatus = "";
        } else {
            workOrderAwaitStatus = recordWorkOrderAwaitStatus;
        }

        let problemFound;
        if (null == recordProblemFound || recordProblemFound.length < 1) {
            problemFound = "";
        } else {
            problemFound = recordProblemFound;
        }

        let summary;
        if (null == recordSummary || recordSummary.length < 1) {
            summary = "";
        } else {
            summary = recordSummary;
        }

        let completionDateTime;
        if (null == recordCompletionDateTime || recordCompletionDateTime.length < 1) {
            completionDateTime = "";
        } else {
            completionDateTime = recordCompletionDateTime;
        }

        document.getElementById('updating-work-order-id').innerText = workOrder.workOrderId;
        document.getElementById('workOrderType').value = workOrder.workOrderType;
        document.getElementById('workOrderAwaitStatus').value = workOrderAwaitStatus;
        document.getElementById('update-problem-reported').value = workOrder.problemReported;
        document.getElementById('update-problem-found').value = workOrder.problemFound;
        document.getElementById('update-summary').value = summary;
        document.getElementById('update-completion-date-time').value = completionDateTime;

        const workOrderDiv = document.getElementById('work-order-display-div');
        const updateWorkOrderDiv = document.getElementById('update-work-order-form-div');
        workOrderDiv.classList.add('hidden');
        updateWorkOrderDiv.classList.remove('hidden');
    }

    async clientLoaded() {
        const urlParams = new URLSearchParams(window.location.search);
        const workOrderId = urlParams.get('workOrderId');
        document.getElementById('work-order-id');
        const workOrder = await this.client.getWorkOrder(workOrderId);
        this.dataStore.set('workOrder', workOrder);
    }

    async addWorkOrderToPage() {
        const workOrder = this.dataStore.get('workOrder');
        if (workOrder == null) {
            return;
        }

        document.getElementById('work-order-id').innerText = workOrder.workOrderId;
        document.getElementById('work-order-type').innerText = workOrder.workOrderType;
        document.getElementById('control-number').innerText = workOrder.controlNumber;
        document.getElementById('serial-number').innerText = workOrder.serialNumber;
        document.getElementById('completion-status').innerText = workOrder.workOrderCompletionStatus;
        document.getElementById('await-status').innerText = workOrder.workOrderAwaitStatus
        document.getElementById('manufacturer').innerText = workOrder.manufacturer
        document.getElementById('model').innerText = workOrder.model
        document.getElementById('facility-name').innerText = workOrder.facilityName
        document.getElementById('assigned-department').innerText = workOrder.assignedDepartment
        document.getElementById('problem-reported').innerText = workOrder.problemReported
        document.getElementById('problem-found').innerText = workOrder.problemFound
        document.getElementById('added-by-id').innerText = workOrder.createdById
        document.getElementById('added-by-name').innerText = workOrder.createdByName
        document.getElementById('created').innerText = workOrder.creationDateTime
        document.getElementById('closed-by-id').innerText = workOrder.closedById
        document.getElementById('closed-by-name').innerText = workOrder.closedByName
        document.getElementById('closed').innerText = workOrder.closedDateTime
        document.getElementById('completed').innerText = workOrder.completionDateTime
        document.getElementById('summary').innerText = workOrder.summary
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