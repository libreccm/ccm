function validate(form) {
  valid = false;
  hasOptions = false;
  for (i = 0; i < form.elements.length; i++) {
    if (form.elements[i].type == "radio") {
      hasOptions = true;
      if (form.elements[i].checked) {
        valid = true;
        break;
      }
    }
  }
        
  if (hasOptions && !valid) {
    alert("Please select an option.");
    return false;
  } else
    return true;
}
