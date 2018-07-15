final class Event
{
   private Action action;
   private long time;
   private WorldEntity entity;

   public Event(Action action, long time, WorldEntity entity)
   {
      this.action = action;
      this.time = time;
      this.entity = entity;
   }

   public Action getAction()
   {
       return action;
   }

   public long getTime()
   {
       return time;
   }

   public WorldEntity getEntity()
   {
       return entity;
   }
}
