package yeelp.stoicdummy.util;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import yeelp.stoicdummy.ModConsts;

public enum Translations {
	INSTANCE;
	
	private Map<String, Translator> cache = Maps.newHashMap();
	
	public class Translator {
		private final String root;
		
		protected Translator(String root) {
			this.root = root;
		}
		
		public ITextComponent getComponent(String key) {
			return new TextComponentTranslation(this.getKey(key));
		}
		
		public ITextComponent getComponent(String key, Object...args) {
			return new TextComponentTranslation(this.getKey(key), args);
		}
		
		public String translate(String key) {
			return this.getComponent(key).getFormattedText();
		}
		
		public String translate(String key, Object...args) {
			return this.getComponent(key, args).getFormattedText();
		}
		
		protected String getKey(String key) {
			return String.format("%s.%s.%s", this.root, ModConsts.MODID, key);
		}
	}
	
	public String translate(String root, String key) {
		return this.getTranslator(root).translate(key);
	}
	
	public Translator getTranslator(String root) {
		return this.cache.compute(root, (r, t) -> t == null ? new Translator(r) : t);
	}
}
