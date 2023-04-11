# AvailableContentStates

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**spaceContentStates** | [**List&lt;ContentState&gt;**](ContentState.md) | Space suggested content states that can be used in the space. This can be null if space content states are disabled in the space. This list can be empty if there are no space content states defined in the space. All spaces start with 3 default space content states, and this can be modified in the UI under space settings. | 
**customContentStates** | [**List&lt;ContentState&gt;**](ContentState.md) | Custom content states that can be used by the user on the content of this call. This can be null if custom content states are disabled in the space of the content. This list can be empty if there are no custom content states defined by the user. This will at most have 3 of the most recently published content states.  Only the calling user has access to place these states on content, but all users can see these states once they are placed. | 
