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

    /**
     * Method for submitting updates when the corresponding button is clicked
     */
    async submitUpdatesWorkOrder(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('update-work-order-error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const successMessageDisplay = document.getElementById('success-message');
        successMessageDisplay.innerText = 'Work order successfully updated.';
        successMessageDisplay.classList.add('hidden');

        // inform the user that the update submission is being processed
        const updateButton = document.getElementById('update-work-order');
        const origButtonText = updateButton.innerText;
        updateButton.innerText = "Updating...";

        // obtain the input values being submitted for update in order to pass to the client
        const workOrderId = document.getElementById('updating-work-order-id').innerText;
        const workOrderType = document.getElementById('workOrderType').value;
        const recordWorkOrderAwaitStatus = document.getElementById('workOrderAwaitStatus').value;
        const problemReported = document.getElementById('update-problem-reported').value;
        const recordProblemFound = document.getElementById('update-problem-found').value;
        const recordSummary = document.getElementById('update-summary').value;
        const recordCompletionDateTime = document.getElementById('update-completion-date-time').value;

        // set optional value to null if empty
        let workOrderAwaitStatus;
        if (null == recordWorkOrderAwaitStatus || recordWorkOrderAwaitStatus.length < 1) {
            workOrderAwaitStatus = "";
        } else {
            workOrderAwaitStatus = recordWorkOrderAwaitStatus;
        }

        // set optional value to null if empty
        let problemFound;
        if (null == recordProblemFound || recordProblemFound.length < 1) {
            problemFound = "";
        } else {
            problemFound = recordProblemFound;
        }

        // set optional value to null if empty
        let summary;
        if (null == recordSummary || recordSummary.length < 1) {
            summary = "";
        } else {
            summary = recordSummary;
        }

        // set optional value to null if empty
        let completionDateTime;
        if (null == recordCompletionDateTime || recordCompletionDateTime.length < 1) {
            completionDateTime = "";
        } else {
            completionDateTime = recordCompletionDateTime;
        }

        // the client call to update the device with the input provided
        const workOrder = await this.client.updateWorkOrder(workOrderId, workOrderType, workOrderAwaitStatus, problemReported, problemFound, summary, completionDateTime, (error) => {
            // reset the button to indicate the process is complete (when an error occurs)
            updateButton.innerText = origButtonText;

            // set and unhide the error message element in this case where an error has occurred
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });

        // if the update succeeds, the update work order form will be hidden and the work order information will be
        // unhidden, but the button text needs to be reset for the next time an attempt will potentially be made
        // to update the work order within this session
        updateButton.innerText = origButtonText;

        // if unsuccessful obtaining the device from the backend, return without updating the device in the datastore
        if (null == workOrder) {
            return;
        }

        // otherwise, update the device in the datastore
        this.dataStore.set('workOrder', workOrder);

        // temporarily display the success message for defined period of time
        successMessageDisplay.classList.remove('hidden');
        setTimeout(() => {
            successMessageDisplay.classList.add('hidden');
        }, 3500);

        // hide the update work order form; unhide the work order record
        const workOrderDiv = document.getElementById('work-order-display-div');
        const updateWorkOrderDiv = document.getElementById('update-work-order-form-div');
        workOrderDiv.classList.remove('hidden');
        updateWorkOrderDiv.classList.add('hidden');
    }

    /**
     * Method for displaying the update work order form, populated with the work order's current values for the information
     * that can potentially be edited
     */
    async displayUpdateWorkOrderForm(evt) {
        evt.preventDefault();

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        // an error message to unhide in the event a backend exception occurs and error message is returned
        const updateWorkOrderErrorMessageDisplay = document.getElementById('update-work-order-error-message');
        updateWorkOrderErrorMessageDisplay.innerText = ``;
        updateWorkOrderErrorMessageDisplay.classList.add('hidden');

        // get the work order from the datastore in order to populate current values on the update work order form
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

        // populate the work order form's fields with the current information to be modified as desired
        document.getElementById('updating-work-order-id').innerText = workOrder.workOrderId;
        document.getElementById('workOrderType').value = workOrder.workOrderType;
        document.getElementById('workOrderAwaitStatus').value = workOrderAwaitStatus;
        document.getElementById('update-problem-reported').value = workOrder.problemReported;
        document.getElementById('update-problem-found').value = workOrder.problemFound;
        document.getElementById('update-summary').value = summary;
        document.getElementById('update-completion-date-time').value = completionDateTime;

        // hide the divs that display the full work order details, while unhiding the update work order form
        const workOrderDiv = document.getElementById('work-order-display-div');
        const updateWorkOrderDiv = document.getElementById('update-work-order-form-div');
        workOrderDiv.classList.add('hidden');
        updateWorkOrderDiv.classList.remove('hidden');
    }

    /**
     * Cancels an update work order form by returning the the work order details view, while hiding the update work order form
     */
    async cancelUpdatesWorkOrder() {
        const workOrderDiv = document.getElementById('work-order-display-div');
        const updateWorkOrderDiv = document.getElementById('update-work-order-form-div');
        updateWorkOrderDiv.classList.add('hidden');
        workOrderDiv.classList.remove('hidden');
    }

    /**
     * When the work order is updated in the datastore, update the work order metadata on the page.
     */
    async addWorkOrderToPage() {
        // get the new work order information
        const workOrder = this.dataStore.get('workOrder');

        // if there is none, do nothing
        if (workOrder == null) {
            return;
        }

        // if the work order is closed, hide the buttons to update and close the work order, since that is no longer applicable
        if (workOrder.workOrderCompletionStatus == "CLOSED") {
            document.getElementById('update-work-order').classList.add('hidden');
            document.getElementById('close-work-order').classList.add('hidden');
        }

        // populate the displayed fields with the metadata
        document.getElementById('work-order-id').innerText = workOrder.workOrderId;
        document.getElementById('work-order-type').innerText = workOrder.workOrderType;
        document.getElementById('control-number').innerHTML = `<a href="device.html?controlNumber=${workOrder.controlNumber}&order=DESCENDING">${workOrder.controlNumber}</a>`;
        document.getElementById('serial-number').innerText = workOrder.serialNumber;
        document.getElementById('completion-status').innerText = workOrder.workOrderCompletionStatus;

        // if the work order is closed, remove the display field intended to show the work order's await status,
        // since that is no longer applicable (the data for this value will be null, by design)
        if (workOrder.workOrderCompletionStatus == "CLOSED") {
            document.getElementById('await-status-field').classList.add('hidden');
        } else {
            document.getElementById('await-status').innerText = workOrder.workOrderAwaitStatus;
        }
        
        // populate the remaining displayed fields with the metadata
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