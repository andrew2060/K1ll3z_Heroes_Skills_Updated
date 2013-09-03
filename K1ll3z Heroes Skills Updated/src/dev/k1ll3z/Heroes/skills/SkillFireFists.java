package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;

public class SkillFireFists extends ActiveSkill
{
  private String igniteText;

  public SkillFireFists(Heroes plugin)
  {
    super(plugin, "FireFists");
    setDescription("Lights your fists ablaze!");
    setUsage("/skill firefists");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill firefists" });
    setTypes(new SkillType[] { SkillType.FIRE, SkillType.HARMFUL, SkillType.BUFF });
    Bukkit.getServer().getPluginManager().registerEvents(new SkillDamageListener(this), plugin);
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.DURATION.node(), Integer.valueOf(30000));
    node.set("fire-ticks", Integer.valueOf(100));
    node.set("ignite-text", "%hero% has lit %target% on fire with his fists!");
    node.set(SkillSetting.EXPIRE_TEXT.node(), "%hero% no longer has firefists!");
    return node;
  }

  public void init()
  {
    super.init();
  }

  public SkillResult use(Hero hero, String[] args)
  {
    int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 30000, false);
    hero.addEffect(new FireFistsEffect(this, duration));
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int fireTicks = SkillConfigManager.getUseSetting(hero, this, "fire-ticks", 20, false);
    return getDescription().replace("$1", fireTicks + "");
  }

  public class FireFistsEffect extends ExpirableEffect
  {
    public FireFistsEffect(Skill skill, long duration)
    {
      super(skill, "FireFistsEffect", duration);
      this.types.add(EffectType.BENEFICIAL);
      this.types.add(EffectType.DISPELLABLE);
      this.types.add(EffectType.FIRE);
    }
  }

  public class SkillDamageListener implements Listener
  {
    private final Skill skill;

    public SkillDamageListener(Skill skill)
    {
      this.skill = skill;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
      if ((event.isCancelled()) || (!(event instanceof EntityDamageByEntityEvent)))
        return;
      EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent)event;
      if (!(subEvent.getDamager() instanceof Player))
        return;
      Player player = (Player)subEvent.getDamager();
      Hero hero = SkillFireFists.this.plugin.getCharacterManager().getHero(player);
      if (!hero.hasEffect("FireFistsEffect")) {
        return;
      }
      int fireTicks = SkillConfigManager.getUseSetting(hero, this.skill, "fire-ticks", 100, false);
      event.getEntity().setFireTicks(fireTicks);
      String name = null;
      if ((event.getEntity() instanceof Player)) {
        name = ((Player)event.getEntity()).getName();
      }
      else if ((event.getEntity() instanceof Creature))
        name = Messaging.getLivingEntityName((Creature)event.getEntity());
      SkillFireFists.this.broadcast(player.getLocation(), SkillFireFists.this.igniteText, new Object[] { 
        player.getDisplayName(), name });
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillFireFists
 * JD-Core Version:    0.6.2
 */