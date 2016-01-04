package openfasttrack.importer.markdown;

@FunctionalInterface
public interface TransitionAction
{
    public abstract void transit();
}