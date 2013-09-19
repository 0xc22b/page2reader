goog.provide('wit.page2reader.PubSub');

goog.require('goog.pubsub.PubSub');


/**
 * Static method to a singleton object of pubsub.
 * @return {goog.pubsub.PubSub}
 */
wit.page2reader.PubSub.getInstance = function() {
  if (!goog.isDefAndNotNull(wit.page2reader.PubSub.pubSub_)) {
    wit.page2reader.PubSub.pubSub_ = new goog.pubsub.PubSub();
  }
  return wit.page2reader.PubSub.pubSub_;
};


/**
 * @type {goog.pubsub.PubSub}
 * @private
 */
wit.page2reader.PubSub.pubSub_;
