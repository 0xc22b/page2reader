goog.provide('wit.fx.dom');
goog.provide('wit.fx.dom.Animation');
goog.provide('wit.fx.dom.Swap');

goog.require('goog.fx.Animation');
goog.require('goog.style');



/**
 * Animation on several properties of an element
 *     with special keywords: show, hide, and toggle.
 * Don't change style after creating an object.
 * @param {Element} element Dom Node to be used in the animation.
 * @param {Object} properties An object of properties to be animated.
 *     Property names must not be renamed by Closure compiler.
 *     Property values must be number in pixel or show|hide|toggle.
 * @param {number} time Length of animation in milliseconds.
 * @param {Function=} opt_acc Acceleration function, returns 0-1 for inputs 0-1.
 * @extends {goog.fx.Animation}
 * @constructor
 */
wit.fx.dom.Animation = function(element, properties, time, opt_acc) {

  /**
   * DOM Node that will be used in the animation
   * @type {Element}
   */
  this.element = element;

  /**
   * Should remove style after animation completed
   * @type {boolean}
   */
  this.shouldRemoveStyle = true;

  /**
   * Hidden this element or not
   * @type {boolean}
   */
  this.isHidden = false;

  /**
   * Array of property names
   * @type {Array.<string>}
   */
  this.propertyNames = [];

  /**
   * Array of units
   * @type {Array.<string>}
   */
  this.propertyUnits = [];

  /**
   * Array for start coordinates
   * @type {Array.<number>}
   */
  this.start = [];

  /**
   * Array for end coordinates
   * @type {Array.<number>}
   */
  this.end = [];

  // Loop through properties
  for (var name in properties) {
    if (properties.hasOwnProperty(name)) {
      var camelCaseName = goog.string.toCamelCase(name);

      // Get current value
      var value = goog.style.getComputedStyle(this.element, camelCaseName);
      var unit = goog.style.getLengthUnits(value);
      var currentValue = parseInt(value, 10);
      // TODO: Broken?
      if (isNaN(currentValue)) {
        unit = 'px';
        currentValue = 0;
      }

      // Get target value
      var targetValue = 0;
      value = properties[name];

      if (value === 'toggle') {
        value = goog.style.isElementShown(element) ? 'hide' : 'show';
      }

      if (goog.isNumber(value)) {
        targetValue = value;

        this.shouldRemoveStyle = false;
      } else if (value === 'show') {
        value = wit.fx.dom.evaluateWithTemporaryDisplay_(
            goog.style.getComputedStyle,
            /** @type {!Element}  */ (element),
            camelCaseName);
        targetValue = parseInt(value, 10);
      } else if (value === 'hide') {
        targetValue = 0;

        if (name === 'width' || name === 'height') {
          this.isHidden = true;
        }
      } else {
        throw Error('Value of properties must be number or ' +
            'special keywords: show, hide, or toggle.');
      }

      this.propertyNames.push(name);
      this.propertyUnits.push(unit);
      this.start.push(currentValue);
      this.end.push(targetValue);
    }
  }

  goog.base(this, this.start, this.end, time, opt_acc);
};
goog.inherits(wit.fx.dom.Animation, goog.fx.Animation);


/** @override */
wit.fx.dom.Animation.prototype.onBegin = function() {

  for (var i = 0; i < this.propertyNames.length; i = i + 1) {
    goog.style.setStyle(this.element, this.propertyNames[i],
        this.start[i] + this.propertyUnits[i]);
  }

  goog.style.setStyle(this.element, 'overflow', 'hidden');
  goog.style.setStyle(this.element, 'display', 'block');

  goog.base(this, 'onBegin');
};


/** @override */
wit.fx.dom.Animation.prototype.onAnimate = function() {

  for (var i = 0; i < this.propertyNames.length; i = i + 1) {
    goog.style.setStyle(this.element, this.propertyNames[i],
        this.coords[i] + this.propertyUnits[i]);
  }

  goog.base(this, 'onAnimate');
};


/** @override */
wit.fx.dom.Animation.prototype.onEnd = function() {

  // Remove if and only if all property values are special keywords
  if (this.shouldRemoveStyle) {
    for (var i = 0; i < this.propertyNames.length; i = i + 1) {
      goog.style.setStyle(this.element, this.propertyNames[i], '');
    }

    goog.style.setStyle(this.element, 'overflow', '');
    goog.style.setStyle(this.element, 'display', this.isHidden ? 'none' : '');
  }

  goog.base(this, 'onEnd');
};


/**
 * A reference to current animation.
 * @type {wit.fx.dom.Animation}
 * @private
 */
wit.fx.dom.anim_;


/**
 * Helper function to create an animation on several properties of an element.
 * One at a time.
 * @param {Element} element Dom Node to be used in the animation.
 * @param {Object} properties An object of properties to be animated.
 *     Property names must not be renamed by Closure compiler.
 *     Property values must be number in pixel or show|hide|toggle.
 * @param {number} time Length of animation in milliseconds.
 * @param {Function=} opt_acc Acceleration function, returns 0-1 for inputs 0-1.
 * @param {Function=} opt_complete Callback function when animation completed.
 * @param {S=} opt_obj The object to be used as the value of 'this'
 *     within opt_complete.
 * @template S
 */
wit.fx.dom.animate = function(element, properties, time, opt_acc,
    opt_complete, opt_obj) {
  if (goog.isDefAndNotNull(wit.fx.dom.anim_)) {
    return;
  }

  wit.fx.dom.anim_ = new wit.fx.dom.Animation(element, properties,
      time, opt_acc);
  goog.events.listen(
      wit.fx.dom.anim_,
      goog.fx.Transition.EventType.END,
      function() {
        goog.events.removeAll(wit.fx.dom.anim_);
        wit.fx.dom.anim_ = null;

        if (goog.isDefAndNotNull(opt_complete)) {
          opt_complete.call(opt_obj);
        }
      },
      false);
  wit.fx.dom.anim_.play(false);
};



/**
 * Swap elements
 * @param {Element} element Dom Node to be used in the animation.
 * @param {number} time Length of animation in milliseconds.
 * @param {Function=} opt_acc Acceleration function, returns 0-1 for inputs 0-1.
 * @extends {goog.fx.Animation}
 * @constructor
 */
wit.fx.dom.Swap = function(element, time, opt_acc) {

  if (goog.dom.getChildren(element).length !== 2) {
    throw Error('Number of children of the element must be 2.');
  }

  /**
   * DOM Node that will be used in the animation as parent element
   * @type {Element}
   */
  this.parent = element;

  /**
   * DOM Node that will be used in the animation as first child
   * @type {Element}
   */
  this.firstChild = goog.dom.getFirstElementChild(element);

  /**
   * DOM Node that will be used in the animation as last child
   * @type {Element}
   */
  this.lastChild = goog.dom.getLastElementChild(element);

  if (goog.style.isElementShown(this.firstChild) == goog.style.isElementShown(
      this.lastChild)) {
    throw Error('One must be shown and the other must be hidden.');
  }

  /**
   * Whether swap from first child to last child
   */
  this.isDown = goog.style.isElementShown(this.firstChild);

  // TODO: getSize not included margin!
  var firstChildHeight = goog.style.getSize(this.firstChild).height;
  var lastChildHeight = goog.style.getSize(this.lastChild).height;

  /**
   * Array for start coordinates
   * @type {Array.<number>}
   */
  this.start = [this.isDown ? firstChildHeight : lastChildHeight,
                this.isDown ? 0 : -1 * firstChildHeight,
                this.isDown ? firstChildHeight : 0];

  /**
   * Array for end coordinates
   * @type {Array.<number>}
   */
  this.end = [this.isDown ? lastChildHeight : firstChildHeight,
              this.isDown ? -1 * firstChildHeight : 0,
              this.isDown ? 0 : firstChildHeight];

  goog.fx.Animation.call(this, this.start, this.end, time, opt_acc);
};
goog.inherits(wit.fx.dom.Swap, goog.fx.Animation);


/** @override */
wit.fx.dom.Swap.prototype.onBegin = function() {

  goog.style.setStyle(this.parent, 'display', 'block');
  goog.style.setStyle(this.parent, 'overflow', 'hidden');
  goog.style.setStyle(this.parent, 'position', 'relative');
  goog.style.setStyle(this.parent, 'height', this.start[0] + 'px');

  goog.style.setStyle(this.firstChild, 'position', 'absolute');
  goog.style.setStyle(this.firstChild, 'top', this.start[1] + 'px');

  goog.style.setStyle(this.lastChild, 'position', 'absolute');
  goog.style.setStyle(this.lastChild, 'top', this.start[2] + 'px');

  if (this.isDown) {
    goog.style.setElementShown(this.lastChild, true);
  } else {
    goog.style.setElementShown(this.firstChild, true);
  }

  goog.base(this, 'onBegin');
};


/** @override */
wit.fx.dom.Swap.prototype.onAnimate = function() {

  goog.style.setStyle(this.parent, 'height', this.coords[0] + 'px');
  goog.style.setStyle(this.firstChild, 'top', this.coords[1] + 'px');
  goog.style.setStyle(this.lastChild, 'top', this.coords[2] + 'px');

  goog.base(this, 'onAnimate');
};


/** @override */
wit.fx.dom.Swap.prototype.onEnd = function() {

  goog.style.setStyle(this.parent, 'display', '');
  goog.style.setStyle(this.parent, 'overflow', '');
  goog.style.setStyle(this.parent, 'position', '');
  goog.style.setStyle(this.parent, 'height', '');

  goog.style.setStyle(this.firstChild, 'position', '');
  goog.style.setStyle(this.firstChild, 'top', '');

  goog.style.setStyle(this.lastChild, 'position', '');
  goog.style.setStyle(this.lastChild, 'top', '');

  if (this.isDown) {
    goog.style.setElementShown(this.firstChild, false);
  } else {
    goog.style.setElementShown(this.lastChild, false);
  }

  goog.base(this, 'onEnd');
};


/**
 * A reference to current swap animation.
 * @type {wit.fx.dom.Swap}
 * @private
 */
wit.fx.dom.swap_;


/**
 * Helper function to create a swap animation. One at a time.
 * @param {Element} element Dom Node to be used in the animation.
 * @param {number} time Length of animation in milliseconds.
 * @param {Function=} opt_acc Acceleration function, returns 0-1 for inputs 0-1.
 */
wit.fx.dom.swap = function(element, time, opt_acc) {
  if (goog.isDefAndNotNull(wit.fx.dom.swap_)) {
    return;
  }

  wit.fx.dom.swap_ = new wit.fx.dom.Swap(element, time, opt_acc);
  goog.events.listen(
      wit.fx.dom.swap_,
      goog.fx.Transition.EventType.END,
      function() {
        goog.events.removeAll(wit.fx.dom.swap_);
        wit.fx.dom.swap_ = null;
      },
      false);
  wit.fx.dom.swap_.play(false);
};


/**
 * Clone of goog.style.evaluateWithTemporaryDisplay_ since it's private.
 * @param {function(!Element, string): T} fn Function to call with
 *     {@code element} as an argument after temporarily changing
 *     {@code element}'s display such that its dimensions are accurate.
 * @param {!Element} element Element (which may have display none) to use as
 *     argument to {@code fn}.
 * @param {string} property Property to get (camel-case).
 * @return {T} Value returned by calling {@code fn} with {@code element}.
 * @template T
 * @private
 */
wit.fx.dom.evaluateWithTemporaryDisplay_ = function(fn, element, property) {
  if (goog.style.getComputedStyle(element, 'display') != 'none') {
    return fn(element, property);
  }

  var style = element.style;
  var originalDisplay = style.display;
  var originalVisibility = style.visibility;
  var originalPosition = style.position;

  style.visibility = 'hidden';
  style.position = 'absolute';
  style.display = 'inline';

  var retVal = fn(element, property);

  style.display = originalDisplay;
  style.position = originalPosition;
  style.visibility = originalVisibility;

  return retVal;
};
