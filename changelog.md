Moved parameter registration and processing to a new class `ParamaterRegister`. The existing parameter system has been
renamed to "Replacement" parameters. 
<br><br>
Added _custom inline event tags_! These can be registered using a similar method to replacement parameters.
Register an event tag using `ParameterRegister.registerEventTag`, providing the tag name, context type (ticking system 
currently supplies: NPC ref `Ref.class`; `PlayerRef.class`, `DialogueMod.class`, `DialoguePageManager.class`). As well 
as a list of parameter names for the tag, and a consuming `EventTagResolver` method which accepts an instance of the
context type and a `Map<String,String>` from the parameter names to the extracted values.

Once the event tag is registered, it can be used with an HTML-like syntax in your `.lang` file. The sound event tags
added in the previous patch have been migrated to use this new system, so there's a prime example in `DialogueRuntime`
showing this in action.
```java
registerEventTag("sound", DialoguePageManager.class, VoiceHandler::playSoundEvent, new String[]{"is"});
```