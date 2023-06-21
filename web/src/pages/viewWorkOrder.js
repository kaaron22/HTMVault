import HTMVaultClient from "../api/htmVaultClient";
import Header from "../components/header";
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

/**
 * Logic needed for the add device page of the website.
 */
class ViewWorkOrder extends BindingClass {
    constructor() {
        super();

        // bind the class methods to this object instance to keep track of state
        this.bindClassMethods(['clientLoaded', 'mount', 'addWorkOrderToPage', 'displayUpdateWorkOrderForm', 'submitUpdatesWorkOrder', 'closeWorkOrder'], this);

         // the datastore to store page information
        this.dataStore = new DataStore();

        // the methods to run when an item in the datastore changes state
        this.dataStore.addChangeListener(this.addWorkOrderToPage);

        // page header, including the home page link and the login/logout button
        this.header = new Header(this.dataStore);

        console.log("view work order constructor");
    }

    /**
     * Once the client is loaded, get the work order metadata
     */
    async clientLoaded() {
        // obtain the work order id from the url
        const urlParams = new URLSearchParams(window.location.search);
        const workOrderId = urlParams.get('workOrderId');

        // notify user that the work order is being loaded
        document.getElementById('work-order-id').innerText = "Loading Work Order...";
        
        // call the client to get the work order
        const workOrder = await this.client.getWorkOrder(workOrderId);

        // if the work order is closed, display/hide the appropriate buttons
        if (workOrder.workOrderCompletionStatus == "CLOSED") {
            document.getElementById('update-work-order').classList.add('hidden');
            document.getElementById('close-work-order').classList.add('hidden');
        }

        // update the datastore        
        this.dataStore.set('workOrder', workOrder);
    }

    /**
     * Add the header to the page, load the HTMVaultClient, and initialize the page information
     */
    mount() {
        document.getElementById('update-work-order').addEventListener('click', this.displayUpdateWorkOrderForm);
        document.getElementById('submit-updates-work-order').addEventListener('click', this.submitUpdatesWorkOrder);
        document.getElementById('close-work-order').addEventListener('click', this.closeWorkOrder);
        document.getElementById('cancel-updates-work-order').addEventListener('click', this.cancelUpdatesWorkOrder);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
    }

    /**
     * Method for closing the work order when the corresponding button is clicked
     */
    async closeWorkOrder(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // a success message to unhide if the endpoint succeeds
        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Work order successfully closed.';
        successMessageDisplay.classList.add('hidden');

        // inform the user that the close work order submission is being processed
        const closeButton = document.getElementById('close-work-order');
        const origButtonText = closeButton.innerText;
        closeButton.innerText = "Closing...";

        // obtain the work order id of the device to pass to the client
        const workOrder = this.dataStore.get('workOrder');
        const workOrderId = workOrder.workOrderId;


        // the client call to close the work order, if conditions are met (i.e. the backend checks for proper completion of the work order, which prevents closing if applicable)
        const closedWorkOrder = await this.client.closeWorkOrder(workOrderId, (error) => {
            // reset the retire device button's text        
            closeButton.innerText = origButtonText;

            // set and display the error message returned
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });

        // avoid updating the datastore with a work order if the process failed
        if (null == closedWorkOrder) {
            return;
        }

        // otherwise, update the datastore
        this.dataStore.set('workOrder', closedWorkOrder);

        // temporarily display a success message, for the time specified
        successMessageDisplay.classList.remove('hidden');
        setTimeout(() => {
            successMessageDisplay.classList.add('hidden');
        }, 3500);
    }

    
    async submitUpdatesWorkOrder(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('update-work-order-error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Work order successfully updated.';
        successMessageDisplay.classList.add('hidden');

        const updateButton = document.getElementById('update-work-order');
        const origButtonText = updateButton.innerText;
        updateButton.innerText = "Updating...";

        const workOrderId = document.getElementById('updating-work-order-id').innerText;
        const workOrderType = document.getElementById('workOrderType').value;
        const recordWorkOrderAwaitStatus = document.getElementById('workOrderAwaitStatus').value;
        const problemReported = document.getElementById('update-problem-reported').value;
        const recordProblemFound = document.getElementById('update-problem-found').value;
        const recordSummary = document.getElementById('update-summary').value;
        const recordCompletionDateTime = document.getElementById('update-completion-date-time').value;

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

        const workOrder = await this.client.updateWorkOrder(workOrderId, workOrderType, workOrderAwaitStatus, problemReported, problemFound, summary, completionDateTime, (error) => {
            updateButton.innerText = origButtonText;
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });

        updateButton.innerText = origButtonText;

        if (null == workOrder) {
            return;
        }

        this.dataStore.set('workOrder', workOrder);

        successMessageDisplay.classList.remove('hidden');
        setTimeout(() => {
            successMessageDisplay.classList.add('hidden');
        }, 3500);

        const workOrderDiv = document.getElementById('work-order-display-div');
        const updateWorkOrderDiv = document.getElementById('update-work-order-form-div');
        workOrderDiv.classList.remove('hidden');
        updateWorkOrderDiv.classList.add('hidden');
    }

    async displayUpdateWorkOrderForm(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const updateWorkOrderErrorMessageDisplay = document.getElementById('update-work-order-error-message');
        updateWorkOrderErrorMessageDisplay.innerText = ``;
        updateWorkOrderErrorMessageDisplay.classList.add('hidden');

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

    async cancelUpdatesWorkOrder() {
        const workOrderDiv = document.getElementById('work-order-display-div');
        const updateWorkOrderDiv = document.getElementById('update-work-order-form-div');
        updateWorkOrderDiv.classList.add('hidden');
        workOrderDiv.classList.remove('hidden');
    }

    async addWorkOrderToPage() {
        const workOrder = this.dataStore.get('workOrder');
        if (workOrder == null) {
            return;
        }

        if (workOrder.workOrderCompletionStatus == "CLOSED") {
            document.getElementById('update-work-order').classList.add('hidden');
            document.getElementById('close-work-order').classList.add('hidden');
        }

        document.getElementById('work-order-id').innerText = workOrder.workOrderId;
        document.getElementById('work-order-type').innerText = workOrder.workOrderType;
        document.getElementById('control-number').innerHTML = `<a href="device.html?controlNumber=${workOrder.controlNumber}&order=DESCENDING">${workOrder.controlNumber}</a>`;
        document.getElementById('serial-number').innerText = workOrder.serialNumber;
        document.getElementById('completion-status').innerText = workOrder.workOrderCompletionStatus;
        if (workOrder.workOrderCompletionStatus == "CLOSED") {
            document.getElementById('await-status-field').classList.add('hidden');
        } else {
            document.getElementById('await-status').innerText = workOrder.workOrderAwaitStatus;
        }
        document.getElementById('manufacturer').innerText = workOrder.manufacturer;
        document.getElementById('model').innerText = workOrder.model;
        document.getElementById('facility-name').innerText = workOrder.facilityName;
        document.getElementById('assigned-department').innerText = workOrder.assignedDepartment;
        document.getElementById('problem-reported').innerText = workOrder.problemReported;
        document.getElementById('problem-found').innerText = workOrder.problemFound;
        document.getElementById('added-by-id').innerText = workOrder.createdById;
        document.getElementById('added-by-name').innerText = workOrder.createdByName;
        document.getElementById('created').innerText = workOrder.creationDateTime;
        document.getElementById('closed-by-id').innerText = workOrder.closedById;
        document.getElementById('closed-by-name').innerText = workOrder.closedByName;
        document.getElementById('closed').innerText = workOrder.closedDateTime;
        document.getElementById('completed').innerText = workOrder.completionDateTime;
        document.getElementById('summary').innerText = workOrder.summary;
    }
}

const main = async () => {
    const viewWorkOrder = new ViewWorkOrder();
    viewWorkOrder.mount();
};

window.addEventListener('DOMContentLoaded', main);