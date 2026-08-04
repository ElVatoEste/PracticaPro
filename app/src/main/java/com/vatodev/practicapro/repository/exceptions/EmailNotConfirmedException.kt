// LATENTE: sin uso mientras BackendGate.isEnabled sea false.
// Reactivación: docs/v2/plan.md, fase R.
package com.vatodev.practicapro.repository.exceptions

class EmailNotConfirmedException(message: String) : Exception(message)

