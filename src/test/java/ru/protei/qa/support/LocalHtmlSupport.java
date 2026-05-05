package ru.protei.qa.support;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.testng.Assert.assertNotNull;

public final class LocalHtmlSupport {
    private LocalHtmlSupport() {
    }

    public static void ensureApplicationScriptsAreAvailable() {
        ensureUIKitAlertStub();

        Boolean jqueryAvailable = executeJavaScript("return typeof window.$ === 'function';");
        if (!Boolean.TRUE.equals(jqueryAvailable)) {
            injectJquerySubset();
            executeInlineApplicationScript();
        }
    }

    private static void ensureUIKitAlertStub() {
        executeJavaScript("window.UIkit = window.UIkit || { modal: { alert: function(message) { window.__lastModalAlert = message; } } };");
    }

    private static void injectJquerySubset() {
        executeJavaScript("""
                (function() {
                  function asArray(value) {
                    return Array.prototype.slice.call(value || []);
                  }

                  function wrap(elements) {
                    return {
                      elements: elements,
                      click: function(handler) {
                        elements.forEach(function(element) {
                          element.addEventListener('click', function(event) {
                            var result = handler.call(element, event);
                            if (result === false) {
                              event.preventDefault();
                              event.stopPropagation();
                            }
                          });
                        });
                        return this;
                      },
                      val: function(value) {
                        if (arguments.length === 0) {
                          return elements[0] ? elements[0].value : undefined;
                        }
                        elements.forEach(function(element) { element.value = value; });
                        return this;
                      },
                      prop: function(name, value) {
                        if (arguments.length === 1) {
                          return elements[0] ? elements[0][name] : undefined;
                        }
                        elements.forEach(function(element) { element[name] = value; });
                        return this;
                      },
                      remove: function() {
                        elements.forEach(function(element) { element.remove(); });
                        return this;
                      },
                      append: function(html) {
                        elements.forEach(function(element) {
                          element.insertAdjacentHTML('beforeend', html);
                        });
                        return this;
                      },
                      hide: function() {
                        elements.forEach(function(element) { element.style.display = 'none'; });
                        return this;
                      },
                      show: function() {
                        elements.forEach(function(element) { element.style.display = ''; });
                        return this;
                      }
                    };
                  }

                  window.$ = function(selector) {
                    if (typeof selector === 'function') {
                      selector();
                      return wrap([]);
                    }
                    if (selector instanceof Element || selector === document || selector === window) {
                      return wrap([selector]);
                    }
                    return wrap(asArray(document.querySelectorAll(selector)));
                  };
                  window.jQuery = window.$;
                })();
                """);
    }

    private static void executeInlineApplicationScript() {
        String applicationScript = executeJavaScript("""
                return Array.from(document.scripts)
                    .map(function(script) { return script.textContent; })
                    .find(function(text) {
                      return text.includes('authButton') && text.includes('dataSend');
                    });
                """);

        assertNotNull(applicationScript, "Application script was not found in HTML");
        executeJavaScript(applicationScript);
    }
}
