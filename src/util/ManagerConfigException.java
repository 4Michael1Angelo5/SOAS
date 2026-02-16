package util;

public class ManagerConfigException extends RuntimeException {
    
    public ManagerConfigException(Class<?> theManager, Class<?> theDataContainer){
         super(String.format(
                """
                        
                        Manager Configuration Error:
                        The %s was configured with %s,
                        but the %s needs indexed based access
                        and %s does not support indexed based access.
                        Please use a DataContainer that supports indexed based access
                        eg an ArrayStore or SinglyLinkedList.
                        """,
                 theManager.getSimpleName(),
                 theDataContainer.getSimpleName(),
                 theManager.getSimpleName(),
                 theDataContainer.getSimpleName()));
    }
}
